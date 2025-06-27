package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private Main() {
    }

    public static void main(final String[] args) {
        final var root = Paths.get(".", "src", "java");
        try (final var stream = Files.walk(root)) {
            final var sources = stream.filter(path -> path.toString().endsWith(".java")).toList();

            Main.runWithSources(sources, root);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static void runWithSources(final Iterable<Path> sources, final Path root) throws IOException {
        for (final var source : sources) {
            final var relative = root.relativize(source.getParent());
            final var input = Files.readString(source);
            final var output = Main.compileRoot(input);
            final var targetParent = Paths.get(".", "src", "node").resolve(relative);
            if (!Files.exists(targetParent))
                Files.createDirectories(targetParent);

            final var fileName = source.getFileName().toString();
            final var separator = fileName.lastIndexOf('.');
            final var name = fileName.substring(0, separator);
            final var target = targetParent.resolve(name + ".ts");
            Files.writeString(target, output);
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
        if (strip.startsWith("package "))
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
                           "}");
    }

    private static String compileStructureSegment(final String input, final CharSequence structName) {
        final var strip = input.strip();
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
        if (input.isEmpty() || '}' != input.charAt(input.length() - 1))
            return Optional.empty();

        final var withoutEnd = input.substring(0, input.length() - "}".length());
        final var contentStart = withoutEnd.indexOf('{');
        if (0 > contentStart)
            return Optional.empty();

        final var before = withoutEnd.substring(0, contentStart).strip();
        final var content = withoutEnd.substring(contentStart + "{".length());
        if (before.isEmpty() || ')' != before.charAt(before.length() - 1))
            return Optional.empty();

        final var withoutParamEnd = before.substring(0, before.length() - ")".length());
        final var paramStart = withoutParamEnd.indexOf('(');
        if (0 > paramStart)
            return Optional.empty();

        final var definition = withoutParamEnd.substring(0, paramStart);
        final var params = withoutParamEnd.substring(paramStart + "(".length());
        final var joinedParams = "(" + Placeholder.generate(params) + ")";
        return Optional.of(Main.parseMethodHeader(definition, structName).generateWithAfterName(joinedParams) + " {" +
                           Main.compileStatements(content, Main::compileFunctionSegment) + "}");
    }

    private static String compileFunctionSegment(final String input) {
        return Main.compileConditional(input)
                   .or(() -> Main.compileStatement(input, Main::compileFunctionStatementValue))
                   .map(value -> System.lineSeparator() + "\t\t" + value).orElseGet(() -> Placeholder.generate(input));
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

    private static Optional<String> compileConditional(final String input) {
        final var strip = input.strip();
        if (!strip.startsWith("if"))
            return Optional.empty();

        final var slice = strip.substring("if".length()).strip();
        if (slice.isEmpty() || '(' != slice.charAt(0))
            return Optional.empty();

        final var substring = slice.substring(1);
        return Main.divide(substring, Main::foldConditional).popFirst().flatMap(Main::compileConditionalSegments);
    }

    private static Optional<String> compileConditionalSegments(final Tuple<String, ListLike<String>> tuple) {
        final var substring1 = tuple.left();
        if (substring1.isEmpty() || ')' != substring1.charAt(substring1.length() - 1))
            return Optional.empty();

        final var condition = substring1.substring(0, substring1.length() - 1);
        final var substring2 = tuple.right().stream().collect(Collectors.joining());
        return Optional.of("if (" + Main.compileValueOrPlaceholder(condition) + ")" + Placeholder.generate(substring2));
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
            return Optional.of(Main.parseDefinitionOrPlaceholder(before).generate() + " = " +
                               Main.compileValueOrPlaceholder(after));
        }
        return Optional.empty();
    }

    private static String compileValueOrPlaceholder(final String input) {
        return Main.compileValue(input).orElseGet(() -> Placeholder.generate(input));
    }

    private static Optional<String> compileValue(final String input) {
        final var arrowIndex = input.indexOf("->");
        if (0 <= arrowIndex) {
            final var before = input.substring(0, arrowIndex).strip();
            if (Main.isSymbol(before)) {
                final var after = input.substring(arrowIndex + "->".length());
                return Main.compileValue(after).map(afterResult -> {
                    return before + " => " + afterResult;
                });
            }
        }

        final var maybeOperator = Main.compileOperator(input, ">=").or(() -> Main.compileOperator(input, "=="))
                                      .or(() -> Main.compileOperator(input, "+"))
                                      .or(() -> Main.compileOperator(input, "<"))
                                      .or(() -> Main.compileOperator(input, "<="))
                                      .or(() -> Main.compileOperator(input, "||"))
                                      .or(() -> Main.compileOperator(input, "!="))
                                      .or(() -> Main.compileOperator(input, "-"))
                                      .or(() -> Main.compileOperator(input, "&&"))
                                      .or(() -> Main.compileOperator(input, ">"));
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
        if (strip.startsWith("!")) {
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

    private static boolean isChar(final String strip) {
        return !strip.isEmpty() && '\'' == strip.charAt(0) && '\'' == strip.charAt(strip.length() - 1) &&
               3 <= strip.length();
    }

    private static Optional<String> compileOperator(final String input, final String operator) {
        final var i = input.indexOf(operator);
        if (0 > i)
            return Optional.empty();

        final var leftSlice = input.substring(0, i);
        final var rightSlice = input.substring(i + operator.length());
        return Main.compileValue(leftSlice).flatMap(left -> {
            return Main.compileValue(rightSlice).map(right -> {
                return left + " " + operator + " " + right;
            });
        });
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
            return Optional.of(new Definition(beforeType, name, Main.compileType(type)));
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
        if (Main.isSymbol(strip))
            return strip;
        return Placeholder.generate(strip);
    }

    private static String compileValues(final String input, final Function<String, String> mapper) {
        return Main.compileAll(input, Main::foldValues, mapper, ", ");
    }

    private static State foldValues(final State state, final char c) {
        if (',' == c && state.isLevel())
            return state.advance();

        final var appended = state.append(c);
        if ('-' == c) {
            final var peek = appended.peek();
            if (peek.isPresent() && peek.get().equals('>'))
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

    private static ListLike<String> divide(final CharSequence input,
                                           final BiFunction<State, Character, State> foldStatements) {
        State current = new MutableState(input);
        while (true) {
            final var maybe = current.pop();
            if (maybe.isEmpty())
                break;

            final var tuple = maybe.get();
            current = tuple.left();
            current = Main.fold(current, tuple.right(), foldStatements);
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

        var current = state.append('\"');
        while (true) {
            final var maybeTuple = current.popAndAppendToTuple();
            if (maybeTuple.isEmpty())
                break;

            final var tuple = maybeTuple.get();
            current = tuple.left();

            final var next = tuple.right();
            if ('\\' == next)
                current = current.popAndAppendToOption().orElse(current);
            if ('\"' == next)
                break;
        }

        return Optional.of(current);

    }

    private static Optional<State> foldSingleQuotes(final State state, final char c) {
        if ('\'' != c)
            return Optional.empty();
        return state.append(c).popAndAppendToTuple().flatMap(
                            tuple -> '\\' == tuple.right() ? tuple.left().popAndAppendToOption() : Optional.of(tuple.left()))
                    .flatMap(State::popAndAppendToOption);
    }

    private static State foldStatements(final State state, final char c) {
        final var appended = state.append(c);
        if (';' == c && appended.isLevel())
            return appended.advance();
        if ('}' == c && appended.isShallow())
            return appended.exit().advance();
        if ('{' == c)
            return appended.enter();
        if ('}' == c)
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
