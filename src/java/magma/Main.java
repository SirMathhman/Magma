package magma;

import magma.divide.DivideState;
import magma.divide.MutableDivideState;
import magma.node.Assignment;
import magma.node.Constructor;
import magma.node.Definable;
import magma.node.Definition;
import magma.node.Header;
import magma.node.Placeholder;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private Main() {}

    public static void main(final String[] args) {
        final var sourceDirectory = Paths.get(".", "src", "java");
        Main.walk(sourceDirectory).match(files -> {
            final var sources = files.stream()
                                     .filter(Files::isRegularFile)
                                     .filter(path -> path.toString().endsWith(".java"))
                                     .collect(Collectors.toSet());

            return Main.runWithSources(sourceDirectory, sources);
        }, Optional::of).ifPresent(Throwable::printStackTrace);
    }

    private static Result<Set<Path>, IOException> walk(final Path sourceDirectory) {
        try (final var stream = Files.walk(sourceDirectory)) {
            return new Ok<>(stream.collect(Collectors.toSet()));
        } catch (final IOException e) {
            return new Err<>(e);
        }
    }

    private static Optional<IOException> runWithSources(final Path sourceDirectory, final Collection<Path> sources) {
        return sources.stream()
                      .map(source -> Main.runWithSource(sourceDirectory, source))
                      .flatMap(Optional::stream)
                      .findFirst();
    }

    private static Optional<IOException> runWithSource(final Path sourceDirectory, final Path source) {
        final var relativeParent = sourceDirectory.relativize(source.getParent());

        final var targetParent = Paths.get(".", "src", "node").resolve(relativeParent);
        if (!Files.exists(targetParent)) return Main.createDirectories(targetParent);

        final var fileName = source.getFileName().toString();
        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);

        final var target = targetParent.resolve(name + ".ts");
        return Main.readString(source).match(input -> {
            final var output = Main.compileStatements(input, Main::compileRootSegment);
            return Main.writeString(target, output);
        }, Optional::of);
    }

    private static Result<String, IOException> readString(final Path source) {
        try {
            return new Ok<>(Files.readString(source));
        } catch (final IOException e) {
            return new Err<>(e);
        }
    }

    private static Optional<IOException> writeString(final Path path, final CharSequence content) {
        try {
            Files.writeString(path, content);
            return Optional.empty();
        } catch (final IOException e) {
            return Optional.of(e);
        }
    }

    private static Optional<IOException> createDirectories(final Path directory) {
        try {
            Files.createDirectories(directory);
            return Optional.empty();
        } catch (final IOException e) {
            return Optional.of(e);
        }
    }

    private static String compileStatements(final CharSequence input, final Function<String, String> mapper) {
        return Main.compileAll(input, mapper, Main::foldStatement, "");
    }

    private static String compileAll(final CharSequence input,
                                     final Function<String, String> mapper,
                                     final BiFunction<DivideState, Character, DivideState> folder,
                                     final CharSequence delimiter) {
        return Main.divide(input, folder).stream().map(mapper).collect(Collectors.joining(delimiter));
    }

    private static String compileRootSegment(final String input) {
        final var stripped = input.strip();
        if (stripped.startsWith("package ")) return "";
        return Main.compileClass(stripped).orElseGet(() -> Placeholder.wrap(stripped) + Main.LINE_SEPARATOR);
    }

    private static Optional<String> compileClass(final String stripped) {
        if (stripped.isEmpty() || '}' != stripped.charAt(stripped.length() - 1)) return Optional.empty();
        final var withoutEnd = stripped.substring(0, stripped.length() - "}".length());

        final var index = withoutEnd.indexOf('{');
        if (0 > index) return Optional.empty();
        final var beforeContent = withoutEnd.substring(0, index);
        final var content = withoutEnd.substring(index + "{".length());

        return Optional.of(Main.compileClassHeader(beforeContent) + " {" +
                           Main.compileStatements(content, Main::compileClassSegment) + "}");
    }

    private static String compileClassSegment(final String input) {
        return Main.LINE_SEPARATOR + "\t" + Main.compileClassSegmentValue(input.strip());
    }

    private static String compileClassSegmentValue(final String input) {
        return Main.compileStatement(input, Main::compileFieldValue)
                   .or(() -> Main.compileMethod(input))
                   .orElseGet(() -> Placeholder.wrap(input));
    }

    private static Optional<String> compileStatement(final String input, final Function<String, String> mapper) {
        if (input.isEmpty() || ';' != input.charAt(input.length() - 1)) return Optional.empty();
        final var slice = input.substring(0, input.length() - ";".length());
        return Optional.of(mapper.apply(slice) + ";");
    }

    private static Optional<String> compileMethod(final String input) {
        final var i = input.indexOf('(');
        if (0 > i) return Optional.empty();
        final var headerString = input.substring(0, i).strip();
        final var substring1 = input.substring(i + "(".length());

        final var i1 = substring1.indexOf(')');
        if (0 > i1) return Optional.empty();
        final var params = substring1.substring(0, i1);
        final var withBraces = substring1.substring(i1 + ")".length()).strip();

        final var maybeHeader = Main.compileDefinition(headerString)
                                    .map(definition -> Main.modifyDefinition(definition, Main::transformFieldModifier))
                                    .<Header>map(value -> value)
                                    .or(() -> Main.compileConstructor(headerString));
        if (withBraces.isEmpty() || '{' != withBraces.charAt(0) || '}' != withBraces.charAt(withBraces.length() - 1))
            return Optional.empty();

        final var content = withBraces.substring(1, withBraces.length() - 1).strip();
        return maybeHeader.flatMap(header -> {
            final var outputParams = "(" + Main.compileParameters(params) + ")";
            return Optional.of(header.generateWithAfterName(outputParams) + " {" +
                               Main.compileStatements(content, Main::compileFunctionSegment) + Main.createIndent(1) +
                               "}");
        });
    }

    private static String compileFunctionSegment(final String input) {
        final var stripped = input.strip();
        if (stripped.isEmpty()) return "";

        return Main.createIndent(2) + Main.compileFunctionSegmentValue(stripped);
    }

    private static String compileFunctionSegmentValue(final String input) {
        return Main.compileStatement(input, Main::compileFunctionStatementValue)
                   .orElseGet(() -> Placeholder.wrap(input));
    }

    private static String compileFunctionStatementValue(final String input) {
        return Main.parseAssignment(input)
                   .map(Main::transformStatementAssignment)
                   .map(Assignment::generate)
                   .orElseGet(() -> Placeholder.wrap(input));
    }

    private static Assignment transformStatementAssignment(final Assignment assignment) {
        return assignment.mapDefinition(Main::transformStatementDefinable);
    }

    private static Definable transformStatementDefinable(final Definable definable) {
        if (!(definable instanceof final Definition definition)) return definable;
        return definition.mapModifiers(Main::transformStatementModifiers).mapType(Main::removeVar);
    }

    private static Optional<String> removeVar(final String type) {
        if ("var".contentEquals(type)) return Optional.empty();
        else return Optional.of(type);
    }

    private static List<String> transformStatementModifiers(final Collection<String> modifiers) {
        final var newModifiers = Main.transformModifiers(modifiers, Main::transformFunctionModifier);
        if (newModifiers.contains("const")) return newModifiers;

        newModifiers.add("let");
        return newModifiers;
    }

    private static Optional<String> transformFunctionModifier(final CharSequence modifier) {
        if ("final".contentEquals(modifier)) return Optional.of("const");
        return Optional.empty();
    }

    private static String createIndent(final int depth) {
        return Main.LINE_SEPARATOR + "\t".repeat(depth);
    }

    private static String compileParameters(final String input) {
        final var stripped = input.strip();
        if (stripped.isEmpty()) return "";

        return Main.compileAll(input, input1 -> Main.transformDefinable(Main.parseDefinable(input1),
                                                                        Main::transformParameterModifier).generate(),
                               Main::foldValue, "");
    }

    private static Optional<String> transformParameterModifier(final String s) {
        return Optional.empty();
    }

    private static DivideState foldValue(final DivideState state, final char c) {
        if (',' == c) return state.advance();
        return state.append(c);
    }

    private static Optional<Constructor> compileConstructor(final String header) {
        final var i2 = header.lastIndexOf(' ');
        if (0 <= i2) {
            final var substring = header.substring(0, i2);
            final var newModifiers = Main.lexModifiers(substring);
            return Optional.of(new Constructor(newModifiers));
        } else return Optional.empty();
    }

    private static String compileFieldValue(final String input) {
        return Main.parseAssignment(input)
                   .map(assignment -> Main.transformAssignment(assignment, Main::transformFieldModifier))
                   .map(Assignment::generate)
                   .orElseGet(() -> Placeholder.wrap(input));
    }

    private static Assignment transformAssignment(final Assignment assignment,
                                                  final Function<String, Optional<String>> mapper) {
        return assignment.mapDefinition(definition -> Main.transformDefinable(definition, mapper));
    }

    private static Optional<Assignment> parseAssignment(final String input) {
        final var index = input.indexOf('=');
        if (0 > index) return Optional.empty();
        final var definition = input.substring(0, index);
        final var value = input.substring(index + "=".length());
        final var definable = Main.parseDefinable(definition);
        final var s = Main.compileValue(value);
        return Optional.of(new Assignment(definable, s));
    }

    private static Definable transformDefinable(final Definable definable,
                                                final Function<String, Optional<String>> transformer) {
        if (definable instanceof final Definition definition) return Main.modifyDefinition(definition, transformer);
        return definable;
    }

    private static Definition modifyDefinition(final Definition definition,
                                               final Function<String, Optional<String>> transformer) {
        return definition.mapModifiers(modifiers -> Main.transformModifiers(modifiers, transformer));
    }

    private static List<String> transformModifiers(final Collection<String> modifiers,
                                                   final Function<String, Optional<String>> transformer) {
        return modifiers.stream().map(transformer).flatMap(Optional::stream).collect(Collectors.toList());
    }

    private static String compileValue(final String input) {
        return Main.compileInvokable(input)
                   .or(() -> Main.compileDataAccess(input))
                   .or(() -> Main.compileIdentifier(input))
                   .or(() -> Main.compileString(input))
                   .orElseGet(() -> Placeholder.wrap(input));
    }

    private static Optional<String> compileString(final String input) {
        final var strip = input.strip();
        if (!strip.isEmpty() && '\"' == strip.charAt(0) && '\"' == input.charAt(input.length() - 1))
            return Optional.of(strip);
        else return Optional.empty();
    }

    private static Optional<String> compileIdentifier(final String input) {
        final var strip = input.strip();
        if (Main.isSymbol(strip)) return Optional.of(strip);
        else return Optional.empty();
    }

    private static boolean isSymbol(final CharSequence input) {
        return IntStream.range(0, input.length()).mapToObj(input::charAt).allMatch(Character::isLetter);
    }

    private static Optional<String> compileDataAccess(final String input) {
        final var i = input.lastIndexOf('.');
        if (0 > i) return Optional.empty();
        final var substring = input.substring(0, i);
        final var substring1 = input.substring(i + ".".length());
        return Optional.of(Main.compileValue(substring) + "." + substring1);
    }

    private static Optional<String> compileInvokable(final String input) {
        final var strip = input.strip();
        if (!(!strip.isEmpty() && ')' == strip.charAt(strip.length() - 1))) return Optional.empty();
        final var slice = strip.substring(0, strip.length() - ")".length());

        final var i = slice.indexOf('(');
        if (0 > i) return Optional.empty();
        final var substring = slice.substring(0, i);
        final var argumentsString = slice.substring(i + "(".length());

        return Optional.of(
                Main.compileValue(substring) + "(" + Main.compileValues(argumentsString, Main::compileValue) + ")");
    }

    private static String compileValues(final CharSequence input, final Function<String, String> mapper) {
        return Main.compileAll(input, mapper, Main::foldValue, ", ");
    }

    private static Definable parseDefinable(final String input) {
        final var beforeType = Main.compileDefinition(input);
        return beforeType.<Definable>map(value -> value).orElseGet(() -> new Placeholder(input));
    }

    private static Optional<Definition> compileDefinition(final String input) {
        final var strip = input.strip();
        final var nameSeparator = strip.lastIndexOf(' ');

        if (0 > nameSeparator) return Optional.empty();
        final var beforeName = strip.substring(0, nameSeparator).strip();
        final var name = strip.substring(nameSeparator + " ".length());

        final var typeSeparator = beforeName.lastIndexOf(' ');
        if (0 > typeSeparator) return Optional.empty();
        final var beforeType = beforeName.substring(0, typeSeparator);
        final var typeString = beforeName.substring(typeSeparator + " ".length());

        final var newModifiers = Main.lexModifiers(beforeType);
        final var type = Main.compileType(typeString);
        return Optional.of(new Definition(newModifiers, name, Optional.of(type)));
    }

    private static Collection<String> lexModifiers(final String modifiers) {
        return Arrays.stream(modifiers.split(" ")).map(String::strip).filter(value -> !value.isEmpty()).toList();
    }

    private static Optional<String> transformFieldModifier(final CharSequence modifier) {
        if ("private".contentEquals(modifier)) return Optional.of("private");
        if ("public".contentEquals(modifier)) return Optional.of("public");
        if ("static".contentEquals(modifier)) return Optional.of("static");
        if ("final".contentEquals(modifier)) return Optional.of("readonly");
        return Optional.empty();
    }

    private static String compileType(final String input) {
        final var strip = input.strip();
        if ("String".contentEquals(strip)) return "string";
        if ("void".contentEquals(strip)) return "void";
        if (strip.endsWith("[]")) {
            final var slice = strip.substring(0, strip.length() - "[]".length());
            return Main.compileType(slice) + "[]";
        }

        return Main.compileIdentifier(input).orElseGet(() -> Placeholder.wrap(input));
    }

    private static String compileClassHeader(final String input) {
        final var index = input.indexOf("class ");
        if (0 <= index) {
            final var beforeKeyword = input.substring(0, index);
            final var afterKeyword = input.substring(index + "class ".length()).strip();
            return Placeholder.wrap(beforeKeyword) + "class " + afterKeyword;
        }

        return Placeholder.wrap(input);
    }

    private static List<String> divide(final CharSequence input,
                                       final BiFunction<DivideState, Character, DivideState> folder) {
        final var state = Main.foldEarly(new MutableDivideState(input), DivideState::pop,
                                         popped -> new Tuple<>(true, Main.foldDecorated(popped, folder)));
        return state.right().advance().stream().toList();
    }

    private static Tuple<Boolean, DivideState> foldEarly(final DivideState initial,
                                                         final Function<DivideState, Optional<Tuple<DivideState, Character>>> mapper,
                                                         final Function<Tuple<DivideState, Character>, Tuple<Boolean, DivideState>> folder) {
        Tuple<Boolean, DivideState> tuple = new Tuple<>(true, initial);
        while (tuple.left()) {
            final var state = tuple.right();
            tuple = Main.foldEarlyElement(state, mapper, folder);
        }
        return tuple;
    }

    private static Tuple<Boolean, DivideState> foldEarlyElement(final DivideState state,
                                                                final Function<DivideState, Optional<Tuple<DivideState, Character>>> mapper,
                                                                final Function<Tuple<DivideState, Character>, Tuple<Boolean, DivideState>> folder) {
        final var maybePopped = mapper.apply(state);
        if (maybePopped.isEmpty()) return new Tuple<>(false, state);
        final var popped = maybePopped.get();
        return folder.apply(popped);
    }

    private static DivideState foldDecorated(final Tuple<DivideState, Character> popped,
                                             final BiFunction<DivideState, Character, DivideState> folder) {
        final var state = popped.left();
        final var c = popped.right();
        return Main.foldSingleQuotes(state, c)
                   .or(() -> Main.foldDoubleQuotes(state, c))
                   .orElseGet(() -> folder.apply(state, c));
    }

    private static Optional<DivideState> foldDoubleQuotes(final DivideState state, final char c) {
        if ('\"' != c) return Optional.empty();
        return Optional.of(
                Main.foldEarly(state.append('\"'), DivideState::popAndAppendToTuple, Main::foldInDoubleQuotes).right());
    }

    private static Tuple<Boolean, DivideState> foldInDoubleQuotes(final Tuple<DivideState, Character> popped) {
        final var nextAppended = popped.left();
        final var next = popped.right();

        if ('\\' == next) return new Tuple<>(true, nextAppended.popAndAppendToOption().orElse(nextAppended));
        if ('\"' == next) return new Tuple<>(false, nextAppended);
        return new Tuple<>(true, nextAppended);
    }

    private static Optional<DivideState> foldSingleQuotes(final DivideState state, final char c) {
        if ('\'' != c) return Optional.empty();
        return state.append(c)
                    .popAndAppendToTuple()
                    .flatMap(Main::foldEscape)
                    .flatMap(DivideState::popAndAppendToOption);
    }

    private static Optional<DivideState> foldEscape(final Tuple<DivideState, Character> tuple) {
        if ('\\' == tuple.right()) return tuple.left().popAndAppendToOption();
        return Optional.of(tuple.left());
    }

    private static DivideState foldStatement(final DivideState state, final char c) {
        final var appended = state.append(c);
        if (';' == c && appended.isLevel()) return appended.advance();
        if ('}' == c && appended.isShallow()) return appended.advance().exit();
        if ('{' == c) return appended.enter();
        if ('}' == c) return appended.exit();
        return appended;
    }
}
