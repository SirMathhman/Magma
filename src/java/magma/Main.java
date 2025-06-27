package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Main {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private Main() {
    }

    public static void main(final String[] args) {
        final var root = Paths.get(".", "src", "java");
        Main.collect(root).match(files -> {
            final var sources = files.stream().filter(path -> path.toString().endsWith(".java")).toList();
            return Main.runWithSources(sources, root);
        }, Optional::of).ifPresent(Throwable::printStackTrace);
    }

    private static Result<List<Path>, IOException> collect(final Path root) {
        try (final var stream = Files.walk(root)) {
            return new Ok<>(stream.toList());
        } catch (final IOException e) {
            return new Err<>(e);
        }
    }

    private static Optional<IOException> runWithSources(final Iterable<Path> sources, final Path root) {
        for (final var source : sources) {
            final var maybe = Main.runWithSource(root, source);
            if (maybe.isPresent())
                return maybe;
        }

        return Optional.empty();
    }

    private static Optional<IOException> runWithSource(final Path root, final Path source) {
        final var relative = root.relativize(source.getParent());
        return Main.readString(source).match(input -> Main.runWithInput(source, input, relative), Optional::of);
    }

    private static Optional<IOException> runWithInput(final Path source, final CharSequence input,
                                                      final Path relative) {
        final var output = Main.compileRoot(input);
        final var targetParent = Paths.get(".", "src", "node").resolve(relative);
        return Main.extracted1(targetParent).or(() -> Main.compileAndWrite(source, targetParent, output));
    }

    private static Optional<IOException> compileAndWrite(final Path source, final Path targetParent,
                                                         final CharSequence output) {
        final var fileName = source.getFileName().toString();
        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);
        final var target = targetParent.resolve(name + ".ts");
        return Main.writeString(target, output);
    }

    private static Optional<IOException> extracted1(final Path targetParent) {
        if (!Files.exists(targetParent))
            return Main.createDirectories(targetParent);
        return Optional.empty();
    }

    private static Optional<IOException> writeString(final Path path, final CharSequence output) {
        try {
            Files.writeString(path, output);
            return Optional.empty();
        } catch (final IOException e) {
            return Optional.of(e);
        }
    }

    private static Optional<IOException> createDirectories(final Path path) {
        try {
            Files.createDirectories(path);
            return Optional.empty();
        } catch (final IOException e) {
            return Optional.of(e);
        }
    }

    private static Result<String, IOException> readString(final Path source) {
        try {
            return new Ok<>(Files.readString(source));
        } catch (final IOException e) {
            return new Err<>(e);
        }
    }

    private static String compileRoot(final CharSequence input) {
        return Main.compileStatements(input, Main::compileRootSegment);
    }

    private static String compileStatements(final CharSequence input, final Function<String, String> mapper) {
        return Main.compileAll(input, Main::foldStatements, mapper, "");
    }

    private static String compileAll(final CharSequence input, final BiFunction<State, Character, State> folder,
                                     final Function<String, String> mapper, final CharSequence delimiter) {
        return Main.divide(input, folder).stream().map(mapper).collect(Collectors.joining(delimiter));
    }

    private static String compileRootSegment(final String input) {
        return Main.compileRootSegmentValue(input.strip()) + Main.LINE_SEPARATOR;
    }

    private static String compileRootSegmentValue(final String input) {
        if (input.isBlank())
            return "";
        return Main.compileNamespaced(input).or(() -> Main.compileStructure(input))
                   .orElseGet(() -> Placeholder.generate(input));
    }

    private static Optional<String> compileNamespaced(final String input) {
        final var strip = input.strip();
        if (strip.startsWith("package ") || strip.startsWith("import "))
            return Optional.of("");
        return Optional.empty();
    }

    private static Optional<String> compileStructure(final String input) {
        if (input.isEmpty() || '}' != input.charAt(input.length() - 1))
            return Optional.empty();

        final var withoutEnd = input.substring(0, input.length() - "}".length());
        final var contentStart = withoutEnd.indexOf('{');
        if (0 > contentStart)
            return Optional.empty();

        final var beforeContent = withoutEnd.substring(0, contentStart);
        final var content = withoutEnd.substring(contentStart + "{".length());
        final var definition = Main.parseStructureHeader(beforeContent);
        final String structName;
        if (definition instanceof final StructureHeader header) {
            if (header.annotations().contains("Actual"))
                return Optional.of("");

            structName = header.name();
        } else
            structName = "?";

        return Optional.of(definition.generate() + " {" +
                           Main.compileStatements(content, input1 -> Main.compileStructureSegment(input1, structName)) +
                           Main.LINE_SEPARATOR + "}");
    }

    private static String compileStructureSegment(final String input, final CharSequence structName) {
        final var strip = input.strip();
        if (strip.isEmpty())
            return "";

        return Main.LINE_SEPARATOR + "\t" + Main.compileStructureSegmentValue(strip, structName);
    }

    private static String compileStructureSegmentValue(final String input, final CharSequence structName) {
        return Main.compileStatement(input, Main::compileAssignment).or(() -> Main.compileMethod(input, structName))
                   .orElseGet(() -> Placeholder.generate(input));
    }

    private static Optional<String> compileStatement(final String input,
                                                     final Function<String, Optional<String>> mapper) {
        if (input.isEmpty() || ';' != input.charAt(input.length() - 1))
            return Optional.empty();

        final var withoutEnd = input.substring(0, input.length() - ";".length());
        return mapper.apply(withoutEnd).map(result -> result + ";");
    }

    private static Optional<String> compileMethod(final String input, final CharSequence structName) {
        final var paramEnd = input.indexOf(')');
        if (0 > paramEnd)
            return Optional.empty();

        final var withParams = input.substring(0, paramEnd);
        final var paramStart = withParams.indexOf('(');
        if (0 > paramStart)
            return Optional.empty();

        final var definition = withParams.substring(0, paramStart);
        final var params = withParams.substring(paramStart + "(".length());
        final var joinedParams = "(" + Main.compileValues(params, Main::compileParameter) + ")";

        final var withBraces = input.substring(paramEnd + ")".length()).strip();
        final String outputContent;
        if (";".contentEquals(withBraces))
            outputContent = ";";
        else if (!withBraces.isEmpty() && '{' == withBraces.charAt(0) &&
                   '}' == withBraces.charAt(withBraces.length() - 1)) {
            final var substring = withBraces.substring(1, withBraces.length() - 1);
            final var compiled = Main.compileFunctionSegments(substring, 2);
            outputContent = " {" + compiled + Main.LINE_SEPARATOR + "\t}";
        } else
            return Optional.empty();

        return Optional.of(
                Main.parseMethodHeader(definition, structName).generateWithAfterName(joinedParams) + outputContent);
    }

    private static String compileFunctionSegments(final CharSequence substring, final int depth) {
        return Main.compileStatements(substring, input -> Main.compileFunctionSegment(input, depth));
    }

    private static String compileParameter(final String input) {
        if (input.isBlank())
            return "";
        return Main.parseDefinitionOrPlaceholder(input).generate();
    }

    private static String compileFunctionSegment(final String input, final int depth) {
        if (input.isBlank())
            return "";
        return Main.compileConditional(input, depth).or(() -> Main.compileElse(input, depth))
                   .or(() -> Main.compileStatement(input, Main::compileFunctionStatementValue))
                   .map(value -> System.lineSeparator() + "\t".repeat(depth) + value)
                   .orElseGet(() -> Placeholder.generate(input));
    }

    private static Optional<String> compileElse(final String input, final int depth) {
        final var strip = input.strip();
        if (strip.startsWith("else")) {
            final var substring = strip.substring("else".length());
            return Optional.of("else " + Main.functionCompileStatementOrBlock(depth, substring));
        } else
            return Optional.empty();
    }

    private static Optional<String> compileFunctionStatementValue(final String input) {
        return Main.compileReturn(input).or(() -> Main.compileAssignment(input));
    }

    private static Optional<String> compileReturn(final String input) {
        final var strip = input.strip();
        if (strip.startsWith("return ")) {
            final var slice = strip.substring("return ".length());
            return Optional.of("return " + Main.compileValueOrPlaceholder(slice));
        }

        return Optional.empty();
    }

    private static Optional<String> compileConditional(final String input, final int depth) {
        final var strip = input.strip();
        if (!strip.startsWith("if"))
            return Optional.empty();

        final var slice = strip.substring("if".length()).strip();
        if (slice.isEmpty() || '(' != slice.charAt(0))
            return Optional.empty();

        final var substring = slice.substring(1);
        return Main.divide(substring, Main::foldConditional).popFirst()
                   .flatMap(tuple -> Main.compileConditionalSegments(tuple, depth));
    }

    private static Optional<String> compileConditionalSegments(final Tuple<String, ListLike<String>> tuple,
                                                               final int depth) {
        final var substring1 = tuple.left();
        if (substring1.isEmpty() || ')' != substring1.charAt(substring1.length() - 1))
            return Optional.empty();

        final var condition = substring1.substring(0, substring1.length() - 1);
        final var joined = tuple.right().stream().collect(Collectors.joining());
        final var compiled = Main.functionCompileStatementOrBlock(depth, joined);
        return Optional.of("if (" + Main.compileValueOrPlaceholder(condition) + ")" + compiled);
    }

    private static String functionCompileStatementOrBlock(final int depth, final String input) {
        final var withBraces = input.strip();
        final String compiled;
        if (Main.isBlock(withBraces)) {
            final var compiled1 =
                    Main.compileFunctionSegments(withBraces.substring(1, withBraces.length() - 1), depth + 1);
            compiled = "{" + compiled1 + Main.LINE_SEPARATOR + "\t".repeat(depth) + "}";
        } else
            compiled = Main.compileFunctionSegment(withBraces, depth + 1);
        return compiled;
    }

    private static boolean isBlock(final CharSequence withBraces) {
        return !withBraces.isEmpty() && '{' == withBraces.charAt(0) &&
               '}' == withBraces.charAt(withBraces.length() - 1);
    }

    private static State foldConditional(final State state, final char c) {
        final var appended = state.append(c);
        if ('(' == c)
            return appended.enter();
        if (')' == c) {
            if (appended.isLevel())
                return appended.advance();
            return appended.exit();
        }
        return appended;
    }

    private static MethodHeader parseMethodHeader(final String input, final CharSequence structName) {
        return Main.parseConstructor(input, structName).orElseGet(() -> Main.parseDefinitionOrPlaceholder(input));
    }

    private static Optional<MethodHeader> parseConstructor(final String input, final CharSequence structName) {
        final var strip = input.strip();
        final var index = strip.lastIndexOf(' ');
        if (0 <= index) {
            final var name = strip.substring(index + " ".length()).strip();
            if (name.contentEquals(structName))
                return Optional.of(new Constructor());
        }

        return Optional.empty();
    }

    private static Optional<String> compileAssignment(final String input) {
        final var separator = input.indexOf('=');
        if (0 <= separator) {
            final var before = input.substring(0, separator);
            final var after = input.substring(separator + "=".length());
            final var assignable = Main.parseDefinitionOrPlaceholder(before);
            final Assignable assignable1;
            if (assignable instanceof final Definition definition)
                assignable1 = definition.withModifier("let");
            else
                assignable1 = assignable;
            return Optional.of(assignable1.generate() + " = " + Main.compileValueOrPlaceholder(after));
        }
        return Optional.empty();
    }

    private static String compileValueOrPlaceholder(final String input) {
        return Main.compileValue(input).orElseGet(() -> Placeholder.generate(input));
    }

    private static Optional<String> compileValue(final String input) {
        final var maybeLambda = Main.compileLambda(input);
        if (maybeLambda.isPresent())
            return maybeLambda;

        final var maybeOperator = Main.compileOperators(input);
        if (maybeOperator.isPresent())
            return maybeOperator;

        final var maybeInvocation = Main.compileInvokable(input);
        if (maybeInvocation.isPresent())
            return maybeInvocation;

        final var separator = input.lastIndexOf('.');
        if (0 <= separator) {
            final var value = input.substring(0, separator);
            final var property = input.substring(separator + ".".length()).strip();
            if (Main.isSymbol(property))
                return Main.compileValue(value).map(result -> result + "." + property);
        }

        final var strip = input.strip();
        if (!strip.isEmpty() && '!' == strip.charAt(0)) {
            final var substring = strip.substring(1);
            return Main.compileValue(substring).map(value -> "!" + value);
        }

        if (Main.isNumber(strip))
            return Optional.of(strip);

        if (!strip.isEmpty() && '\"' == strip.charAt(0) && '\"' == strip.charAt(strip.length() - 1))
            return Optional.of(strip);

        if (Main.isChar(strip))
            return Optional.of(strip);

        if (Main.isSymbol(strip))
            return Optional.of(strip);

        return Optional.empty();
    }

    private static Optional<String> compileLambda(final String input) {
        final var arrowIndex = input.indexOf("->");
        if (0 > arrowIndex)
            return Optional.empty();

        final var before = input.substring(0, arrowIndex).strip();
        if (!Main.isSymbol(before))
            return Optional.empty();

        final var after = input.substring(arrowIndex + "->".length());
        return Main.compileValue(after).map(afterResult -> before + " => " + afterResult);
    }

    private static Optional<String> compileOperators(final String input) {
        return Main.compileOperator(input, ">=").or(() -> Main.compileOperator(input, "=="))
                   .or(() -> Main.compileOperator(input, "+")).or(() -> Main.compileOperator(input, "<"))
                   .or(() -> Main.compileOperator(input, "<=")).or(() -> Main.compileOperator(input, "||"))
                   .or(() -> Main.compileOperator(input, "!=")).or(() -> Main.compileOperator(input, "-"))
                   .or(() -> Main.compileOperator(input, "&&")).or(() -> Main.compileOperator(input, ">"));
    }

    private static boolean isChar(final CharSequence strip) {
        return !strip.isEmpty() && '\'' == strip.charAt(0) && '\'' == strip.charAt(strip.length() - 1) &&
               3 <= strip.length();
    }

    private static Optional<String> compileOperator(final String input, final String operator) {
        final var i = input.indexOf(operator);
        if (0 > i)
            return Optional.empty();

        final var leftSlice = input.substring(0, i);
        final var rightSlice = input.substring(i + operator.length());
        return Main.compileValue(leftSlice)
                   .flatMap(left -> Main.compileValue(rightSlice).map(right -> left + " " + operator + " " + right));
    }

    private static Optional<String> compileInvokable(final String input) {
        final var strip = input.strip();
        if (strip.isEmpty() || ')' != strip.charAt(strip.length() - 1))
            return Optional.empty();

        final var withoutEnd = strip.substring(0, strip.length() - ")".length());
        return Main.divide(withoutEnd, Main::foldInvocation).popLast().flatMap(Main::handleInvocationSegments);
    }

    private static State foldInvocation(final State state, final char c) {
        final var appended = state.append(c);
        if ('(' == c) {
            final var entered = appended.enter();
            if (entered.isShallow())
                return entered.advance();
            else
                return entered;
        }
        if (')' == c)
            return appended.exit();
        return appended;
    }

    private static boolean isSymbol(final CharSequence input) {
        final var length = input.length();
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            if (Character.isLetter(c) || (0 != i && Character.isDigit(c)))
                continue;
            return false;
        }
        return true;
    }

    private static boolean isNumber(final CharSequence input) {
        final var length = input.length();
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            if (!Character.isDigit(c))
                return false;
        }
        return true;
    }

    private static Assignable parseDefinitionOrPlaceholder(final String input) {
        final var strip = input.strip();
        return Main.parseDefinition(strip).<Assignable>map(value -> value).orElseGet(() -> new Placeholder(strip));
    }

    private static Optional<Definition> parseDefinition(final String strip) {
        final var separator = strip.lastIndexOf(' ');
        if (0 > separator)
            return Optional.empty();

        final var beforeName = strip.substring(0, separator);
        final var name = strip.substring(separator + " ".length());

        final var divisions = Main.divide(beforeName, Main::foldTypeSeparator);
        return divisions.popLast().flatMap(tuple -> {
            final var beforeType = tuple.left().stream().collect(Collectors.joining(" "));
            final var type = tuple.right();
            return Optional.of(new Definition(Lists.empty(), beforeType, name, Main.compileType(type)));
        });
    }

    private static State foldTypeSeparator(final State state, final Character c) {
        if (' ' == c && state.isLevel())
            return state.advance();

        final var appended = state.append(c);
        if ('<' == c)
            return appended.enter();
        if ('>' == c)
            return appended.exit();
        return appended;
    }

    private static String compileType(final String input) {
        final var strip = input.strip();
        if ("var".contentEquals(strip))
            return "any";
        if ("String".contentEquals(strip))
            return "string";
        if ("int".contentEquals(strip))
            return "number";
        if (!strip.isEmpty() && '>' == strip.charAt(strip.length() - 1)) {
            final var withoutEnd = strip.substring(0, strip.length() - ">".length());
            final var start = withoutEnd.indexOf('<');
            if (0 <= start) {
                final var base = withoutEnd.substring(0, start);
                final var argument = withoutEnd.substring(start + "<".length());
                final var compiled = Main.compileValues(argument, Main::compileType);
                return base + "<" + compiled + ">";
            }
        }

        if (strip.endsWith("[]")) {
            final var slice = strip.substring(0, strip.length() - "[]".length());
            final var compiled = Main.compileType(slice);
            return compiled + "[]";
        }

        if (Main.isSymbol(strip))
            return strip;

        return Placeholder.generate(strip);
    }

    private static String compileValues(final CharSequence input, final Function<String, String> mapper) {
        return Main.compileAll(input, Main::foldValues, mapper, ", ");
    }

    private static State foldValues(final State state, final char c) {
        if (',' == c && state.isLevel())
            return state.advance();

        final var appended = state.append(c);
        if ('-' == c) {
            final var peek = appended.peek();
            if (peek.filter(value -> '>' == value).isPresent())
                return appended.popAndAppendToOption().orElse(appended);
        }

        if ('<' == c || '(' == c)
            return appended.enter();
        if ('>' == c || ')' == c)
            return appended.exit();
        return appended;
    }

    private static StructureDefinition parseStructureHeader(final String input) {
        return Main.parseClassHeader(input, "class", "class").or(() -> Main.parseClassHeader(input, "record", "class"))
                   .or(() -> Main.parseClassHeader(input, "interface", "interface"))
                   .orElseGet(() -> new Placeholder(input));
    }

    private static Optional<StructureDefinition> parseClassHeader(final String input, final String keyword,
                                                                  final String type) {
        final var classIndex = input.indexOf(keyword + " ");
        if (0 > classIndex)
            return Optional.empty();

        final var beforeKeyword = input.substring(0, classIndex).strip();
        final var afterKeyword = input.substring(classIndex + (keyword + " ").length()).strip();
        final var implementsIndex = afterKeyword.indexOf("implements ");
        if (0 <= implementsIndex) {
            final var beforeImplements = afterKeyword.substring(0, implementsIndex).strip();
            final var afterImplements = afterKeyword.substring(implementsIndex + "implements ".length()).strip();
            return Optional.of(Main.complete(type, beforeKeyword, beforeImplements, Optional.of(afterImplements)));
        } else
            return Optional.of(Main.complete(type, beforeKeyword, afterKeyword, Optional.empty()));
    }

    private static StructureHeader complete(final String type, final String beforeKeyword,
                                            final String beforeImplements, final Optional<String> maybeImplements) {
        final var strip = beforeImplements.strip();
        if (!strip.isEmpty() && ')' == strip.charAt(strip.length() - 1)) {
            final var withoutEnd = strip.substring(0, strip.length() - ")".length());
            final var contentStart = withoutEnd.indexOf('(');
            if (0 <= contentStart) {
                final var strip1 = withoutEnd.substring(0, contentStart).strip();
                return Main.parseStructureHeaderByAnnotations(type, beforeKeyword, maybeImplements, strip1);
            }
        }

        return Main.parseStructureHeaderByAnnotations(type, beforeKeyword, maybeImplements, beforeImplements);
    }

    private static StructureHeader parseStructureHeaderByAnnotations(final String type, final String beforeKeyword,
                                                                     final Optional<String> maybeImplements,
                                                                     final String strip1) {
        final var index = beforeKeyword.lastIndexOf(System.lineSeparator());
        if (0 <= index) {
            final var annotations =
                    Arrays.stream(Pattern.compile("\\n").split(beforeKeyword.substring(0, index).strip()))
                          .map(String::strip).filter(value -> !value.isEmpty()).map(value -> value.substring(1))
                          .toList();

            final var substring1 = beforeKeyword.substring(index + System.lineSeparator().length());
            return new StructureHeader(type, annotations, substring1, strip1, maybeImplements);
        }

        return new StructureHeader(type, Collections.emptyList(), beforeKeyword, strip1, maybeImplements);
    }

    private static ListLike<String> divide(final CharSequence input, final BiFunction<State, Character, State> folder) {
        State current = new MutableState(input);
        while (true) {
            final var maybe = current.pop().toTuple(new Tuple<>(current, '\0'));
            if (maybe.left()) {
                final var tuple = maybe.right();
                current = tuple.left();
                current = Main.fold(current, tuple.right(), folder);
            } else
                break;
        }

        return current.advance().unwrap();
    }

    private static State fold(final State state, final char c, final BiFunction<State, Character, State> folder) {
        return Main.foldSingleQuotes(state, c).or(() -> Main.foldDoubleQuotes(state, c))
                   .orElseGet(() -> folder.apply(state, c));
    }

    private static Optional<State> foldDoubleQuotes(final State state, final char c) {
        if ('\"' != c)
            return Optional.empty();

        final var current = state.append('\"');
        while (true) {
            final var maybeTuple =
                    current.popAndAppendToTuple().flatMap(tuple -> Main.getObjectOptional(tuple, current));
            if (maybeTuple.isEmpty())
                break;
        }

        return Optional.of(current);

    }

    private static Optional<State> getObjectOptional(final Tuple<State, Character> tuple, final State current) {
        final var left = tuple.left();

        final var next = tuple.right();
        if ('\\' == next)
            return Optional.of(left.popAndAppendToOption().orElse(current));
        if ('\"' == next)
            return Optional.empty();

        return Optional.of(left);
    }

    private static Optional<State> foldSingleQuotes(final State state, final char c) {
        if ('\'' != c)
            return Optional.empty();
        return state.append(c).popAndAppendToTuple().flatMap(tuple -> {
            if ('\\' == tuple.right())
                return tuple.left().popAndAppendToOption();
            return Optional.of(tuple.left());
        }).flatMap(State::popAndAppendToOption);
    }

    private static State foldStatements(final State state, final char c) {
        final var appended = state.append(c);
        if (';' == c && appended.isLevel())
            return appended.advance();
        if ('}' == c && appended.isShallow())
            return appended.exit().advance();
        if ('{' == c || '(' == c)
            return appended.enter();
        if ('}' == c || ')' == c)
            return appended.exit();
        return appended;
    }

    private static Optional<String> handleInvocationSegments(final Tuple<ListLike<String>, String> tuple) {
        final var joined = tuple.left().stream().collect(Collectors.joining());
        if (joined.isEmpty() || '(' != joined.charAt(joined.length() - 1))
            return Optional.empty();

        final var substring = joined.substring(0, joined.length() - "(".length());
        final var argument = tuple.right();
        return Main.compileCaller(substring)
                   .map(caller -> caller + "(" + Main.compileValues(argument, Main::compileValueOrPlaceholder) + ")");
    }

    private static Optional<String> compileCaller(final String input) {
        final var strip = input.strip();
        if (strip.startsWith("new ")) {
            final var substring = strip.substring("new ".length());
            return Optional.of("new " + Main.compileType(substring));
        }

        return Main.compileValue(strip);
    }
}
