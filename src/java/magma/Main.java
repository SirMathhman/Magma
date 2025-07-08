package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private Main() {}

    public static void main(final String[] args) {
        final var sourceDirectory = Paths.get(".", "src", "java");
        try (final var stream = Files.walk(sourceDirectory)) {
            final var sources = stream.filter(Files::isRegularFile)
                                      .filter(path -> path.toString().endsWith(".java"))
                                      .collect(Collectors.toSet());

            Main.runWithSources(sourceDirectory, sources);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static void runWithSources(final Path sourceDirectory, final Iterable<Path> sources) throws IOException {
        for (final var source : sources) Main.runWithSource(sourceDirectory, source);
    }

    private static void runWithSource(final Path sourceDirectory, final Path source) throws IOException {
        final var relativeParent = sourceDirectory.relativize(source.getParent());
        final var fileName = source.getFileName().toString();
        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);
        final var targetParent = Paths.get(".", "src", "node").resolve(relativeParent);
        if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

        final var target = targetParent.resolve(name + ".ts");
        final var input = Files.readString(source);
        final var csq = Main.compileStatements(input, Main::compileRootSegment);
        Files.writeString(target, csq);
    }

    private static String compileStatements(final CharSequence input, final Function<String, String> mapper) {
        final var segments = Main.divide(input);
        final var output = new StringBuilder();
        for (final var segment : segments) output.append(mapper.apply(segment));
        return output.toString();
    }

    private static String compileRootSegment(final String input) {
        final var strip = input.strip();
        if (strip.startsWith("package ") || strip.startsWith("import ")) return "";
        return Main.compileClass(strip).orElseGet(() -> Main.generatePlaceholder(strip));
    }

    private static Optional<String> compileClass(final String strip) {
        return Main.compileSuffix(strip, "}", Main::getString);
    }

    private static Optional<String> compileSuffix(final String input,
                                                  final String suffix,
                                                  final Function<String, Optional<String>> mapper) {
        if (!input.endsWith(suffix)) return Optional.empty();
        final var withoutEnd = input.substring(0, input.length() - suffix.length());
        return mapper.apply(withoutEnd);
    }

    private static Optional<String> getString(final String input) {
        return Main.compileFirst(input, "{", (beforeContent1, content1) -> Optional.of(
                Main.compileClassHeader(beforeContent1) + "{" +
                Main.compileStatements(content1, input1 -> Main.compileClassSegment(input1, "?")) +
                Main.LINE_SEPARATOR + "}"));
    }

    private static Optional<String> compileFirst(final String withoutEnd,
                                                 final String infix,
                                                 final BiFunction<String, String, Optional<String>> mapper) {
        return Main.compileInfix(withoutEnd, Main::findFirst, infix, mapper);
    }

    private static Optional<String> compileInfix(final String withoutEnd,
                                                 final BiFunction<String, String, Optional<Integer>> locator,
                                                 final String infix,
                                                 final BiFunction<String, String, Optional<String>> mapper) {
        return Main.getString(withoutEnd, locator, infix, s -> new StringRule("left").lex(s),
                              s -> new StringRule("right").lex(s), node -> new StringRule("left").generate(node)
                                                                                                 .flatMap(left -> new StringRule(
                                                                                                         "right").generate(node)
                                                                                                                          .flatMap(
                                                                                                        right -> mapper.apply(
                                                                                                                left,
                                                                                                                right))));
    }

    private static Optional<String> getString(final String input,
                                              final BiFunction<String, String, Optional<Integer>> locator,
                                              final String infix,
                                              final Function<String, Optional<Node>> leftMapper,
                                              final Function<String, Optional<Node>> rightMapper,
                                              final Function<Node, Optional<String>> completer) {
        return locator.apply(input, infix).flatMap(index -> {
            final var leftString = input.substring(0, index);
            final var rightString = input.substring(index + infix.length());
            return leftMapper.apply(leftString)
                             .flatMap(leftResult -> rightMapper.apply(rightString).map(leftResult::merge));
        }).flatMap(completer);
    }

    private static Optional<Integer> findFirst(final String input, final String infix) {
        final var index = input.indexOf(infix);
        if (0 > index) return Optional.empty();
        return Optional.of(index);
    }

    private static String compileClassHeader(final String input) {
        return Main.compileFirst(input, "class ", Main::compileModifiers)
                   .orElseGet(() -> Main.generatePlaceholder(input));
    }

    private static Optional<String> compileModifiers(final String modifiers, final String name) {
        final var stripped = modifiers.strip();
        final String newModifiers;
        if ("public".contentEquals(stripped)) newModifiers = "export ";
        else newModifiers = "";
        return Optional.of(Main.generate(new MapNode().withString("name", name).withString("modifiers", newModifiers)));
    }

    private static String generate(final Node node) {
        return new StringRule("modifiers").generate(node).orElse("") + "class " + new StringRule(
                "name").generate(node).orElse("");
    }

    private static String compileClassSegment(final String input, final String structName) {
        return Main.LINE_SEPARATOR + "\t" + Main.compileClassSegmentValue(input, structName);
    }

    private static String compileClassSegmentValue(final String input, final String structName) {
        return Main.compileSuffix(input.strip(), ";", s -> Optional.of(Main.compileClassStatementValue(s) + ";"))
                   .or(() -> Main.compileMethod(input, structName))
                   .orElseGet(() -> Main.generatePlaceholder(input));
    }

    private static Optional<String> compileMethod(final String input, final String structName) {
        return Main.compileFirst(input, "(", (header, s2) -> Optional.of(
                Main.compileMethodHeader(header.strip(), structName) + "(" + Main.generatePlaceholder(s2)));
    }

    private static String compileMethodHeader(final String input, final String structName) {
        return Main.compileLast(input.strip(), " ", (s, name) -> {
            if (name.contentEquals(structName)) return Optional.of(Main.generatePlaceholder(s) + " constructor");
            else return Optional.empty();
        }).orElseGet(() -> {
            return Main.generatePlaceholder(input);
        });
    }

    private static String compileClassStatementValue(final String input) {
        return Main.compileFirst(input, "=", (definition, value) -> Optional.of(
                           Main.compileDefinition(definition) + " = " + Main.compileValue(value)))
                   .orElseGet(() -> Main.generatePlaceholder(input));
    }

    private static String compileValue(final String input) {
        return Main.compileInvokable(input)
                   .or(() -> Main.compileAccess(input))
                   .or(() -> Main.compileSymbol(input))
                   .orElseGet(() -> Main.generatePlaceholder(input));
    }

    private static Optional<String> compileSymbol(final String input) {
        final var strip = input.strip();
        if (Main.isSymbol(strip)) return Optional.of(strip);
        else return Optional.empty();
    }

    private static boolean isSymbol(final String input) {
        return IntStream.range(0, input.length()).map(input::codePointAt).allMatch(Character::isLetter);
    }

    private static Optional<String> compileAccess(final String input) {
        return Main.compileLast(input, ".", (s, s2) -> Optional.of(Main.compileValue(s) + "." + s2));
    }

    private static Optional<String> compileInvokable(final String input) {
        return Main.compileSuffix(input.strip(), ")", withoutEnd -> Main.compileFirst(withoutEnd, "(",
                                                                                      (s, s2) -> Optional.of(
                                                                                              Main.compileValue(s) +
                                                                                              "(" +
                                                                                              Main.compileArguments(
                                                                                                      s2) + ")")));
    }

    private static String compileArguments(final String input) {
        final var strip = input.strip();
        if (strip.isEmpty()) return "";
        return Main.generatePlaceholder(strip);
    }

    private static String compileDefinition(final String input) {
        return Main.compileLast(input.strip(), " ", (beforeName, name) -> Main.compileLast(beforeName.strip(), " ",
                                                                                           (modifiersString, type) -> Main.getString(
                                                                                                   name,
                                                                                                   modifiersString,
                                                                                                   type)))
                   .orElseGet(() -> Main.generatePlaceholder(input));
    }

    private static Optional<String> getString(final String name, final String modifiersString, final String type) {
        final var oldModifiers = Arrays.stream(modifiersString.split(" "))
                                       .map(String::strip)
                                       .filter(value -> !value.isEmpty())
                                       .collect(Collectors.toSet());
        final var newModifiers = Main.replaceModifiers(oldModifiers);
        final var joined = newModifiers.stream().map(value -> value + " ").collect(Collectors.joining());
        return Optional.of(joined + name + " : " + Main.compileType(type));
    }

    private static String compileType(final String input) {
        final var strip = input.strip();
        if ("String".contentEquals(strip)) return "string";

        return Main.generatePlaceholder(strip);
    }

    private static List<String> replaceModifiers(final Collection<String> oldModifiers) {
        return oldModifiers.stream().map(Main::retainModifier).flatMap(Optional::stream).toList();
    }

    private static Optional<String> retainModifier(final String modifier) {
        return switch (modifier) {
            case "private" -> Optional.of("private");
            case "static" -> Optional.of("static");
            case "final" -> Optional.of("readonly");
            default -> Optional.empty();
        };
    }

    private static Optional<String> compileLast(final String input,
                                                final String infix,
                                                final BiFunction<String, String, Optional<String>> mapper) {
        return Main.compileInfix(input, Main::findLast, infix, mapper);
    }

    private static Optional<Integer> findLast(final String input, final String infix) {
        final var index = input.lastIndexOf(infix);
        if (-1 == index) return Optional.empty();
        return Optional.of(index);
    }

    private static List<String> divide(final CharSequence input) {
        var current = (DivideState) new MutableDivideState();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = Main.fold(current, c);
        }

        return current.advance().stream().toList();
    }

    private static DivideState fold(final DivideState state, final char c) {
        final var appended = state.append(c);
        if (';' == c && appended.isLevel()) return appended.advance();
        if ('}' == c && appended.isShallow()) return appended.advance().exit();
        if ('{' == c) return appended.enter();
        if ('}' == c) return appended.exit();
        return appended;
    }

    private static String generatePlaceholder(final String input) {
        final var replaced = input.replace("/*", "start").replace("*/", "end");
        return "/*" + replaced + "*/";
    }
}
