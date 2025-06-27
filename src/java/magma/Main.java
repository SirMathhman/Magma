package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final Pattern PATTERN = Pattern.compile("\\n");

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
        return Main.divide(input).stream().map(mapper).collect(Collectors.joining());
    }

    private static String compileRootSegment(final String input) {
        return Main.compileRootSegmentValue(input.strip()) + Main.LINE_SEPARATOR;
    }

    private static String compileRootSegmentValue(final String input) {
        return Main.compileStructure(input).orElseGet(() -> Placeholder.generate(input));
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

    private static String compileStructureSegment(final String input, final String structName) {
        final var strip = input.strip();
        return Main.LINE_SEPARATOR + "\t" + Main.compileStructureSegmentValue(strip, structName);
    }

    private static String compileStructureSegmentValue(final String input, final String structName) {
        return Main.compileField(input).or(() -> Main.compileMethod(input, structName))
                   .orElseGet(() -> Placeholder.generate(input));
    }

    private static Optional<String> compileField(final String input) {
        if (input.isEmpty() || ';' != input.charAt(input.length() - 1))
            return Optional.empty();

        final var withoutEnd = input.substring(0, input.length() - ";".length());
        return Main.compileStructureStatementValue(withoutEnd).map(result -> result + ";");
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
        return Placeholder.generate(input);
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

    private static Optional<String> compileStructureStatementValue(final String input) {
        final var separator = input.indexOf('=');
        if (0 <= separator) {
            final var before = input.substring(0, separator);
            final var after = input.substring(separator + "=".length());
            return Optional.of(Main.parseDefinitionOrPlaceholder(before).generate() + " = " + Main.compileValue(after));
        }
        return Optional.empty();
    }

    private static String compileValue(final String input) {
        final var strip = input.strip();
        if (!strip.isEmpty() && ')' == strip.charAt(strip.length() - 1)) {
            final var substring = strip.substring(0, strip.length() - ")".length());
            final var i = substring.indexOf('(');
            if (0 <= i) {
                final var caller = substring.substring(0, i);
                final var argument = substring.substring(i + "(".length());
                return Main.compileValue(caller) + "(" + Placeholder.generate(argument) + ")";
            }
        }

        final var separator = input.lastIndexOf('.');
        if (0 <= separator) {
            final var substring = input.substring(0, separator);
            final var substring1 = input.substring(separator + ".".length()).strip();
            return Main.compileValue(substring) + "." + substring1;
        }

        if (Main.isNumber(strip))
            return strip;

        if (!strip.isEmpty() && '\"' == strip.charAt(0) && '\"' == strip.charAt(strip.length() - 1))
            return strip;

        if (Main.isSymbol(strip))
            return strip;

        return Placeholder.generate(strip);
    }

    private static boolean isSymbol(final CharSequence input) {
        final var length = input.length();
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            if (Character.isLetter(c))
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
        final var typeSeparator = beforeName.lastIndexOf(' ');
        if (0 > typeSeparator)
            return Optional.empty();

        final var beforeType = beforeName.substring(0, typeSeparator);
        final var type = beforeName.substring(typeSeparator + " ".length());
        return Optional.of(new Definition(beforeType, name, Main.compileType(type)));
    }

    private static String compileType(final String input) {
        final var strip = input.strip();
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
                return base + "<" + Main.compileType(argument) + ">";
            }
        }
        return Placeholder.generate(strip);
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
                    Arrays.stream(Main.PATTERN.split(beforeKeyword.substring(0, index).strip())).map(String::strip)
                          .filter(value -> !value.isEmpty()).map(value -> value.substring(1)).toList();

            final var substring1 = beforeKeyword.substring(index + System.lineSeparator().length());
            return new StructureHeader(type, annotations, substring1, strip1, maybeImplements);
        }

        return new StructureHeader(type, Collections.emptyList(), beforeKeyword, strip1, maybeImplements);
    }

    private static ListLike<String> divide(final CharSequence input) {
        State current = new MutableState(input);
        while (true) {
            final var maybe = current.pop();
            if (maybe.isEmpty())
                break;

            final var tuple = maybe.get();
            current = tuple.left();
            current = Main.fold(current, tuple.right());
        }

        return current.advance().unwrap();
    }

    private static State fold(final State state, final char c) {
        return Main.foldSingleQuotes(state, c).or(() -> Main.foldDoubleQuotes(state, c))
                   .orElseGet(() -> Main.foldStatements(state, c));
    }

    private static Optional<State> foldDoubleQuotes(final State state, final char c) {
        if ('\"' == c) {
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

        return Optional.empty();
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
}
