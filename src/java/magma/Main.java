package magma;

import magma.rule.InfixRule;
import magma.rule.Rule;
import magma.rule.StringRule;
import magma.state.DivideState;
import magma.state.MutableDivideState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        final var fileName = source.getFileName().toString();
        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);

        final var relativeParent = sourceDirectory.relativize(source.getParent());
        final var targetDirectory = Paths.get(".", "src", "node");
        final var targetParent = targetDirectory.resolve(relativeParent);
        if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

        final var target = targetParent.resolve(name + ".ts");
        final var input = Files.readString(source);

        final var segments = Main.divide(input);

        final var output = new StringBuilder();
        for (final var segment : segments) output.append(Main.compileRootSegment(segment));

        Files.writeString(target, output);
    }

    private static String compileRootSegment(final String input) {
        final var strip = input.strip();
        if (strip.startsWith("package ")) return "";
        return Main.compileClass(input).orElseGet(() -> Main.generatePlaceholder(input.strip()) + Main.LINE_SEPARATOR);
    }

    private static Optional<String> compileClass(final String input) {
        final var strip = input.strip();
        if (strip.isEmpty() || '}' != strip.charAt(strip.length() - 1)) return Optional.empty();
        final var withoutEnd = strip.substring(0, strip.length() - "}".length());

        final var i = withoutEnd.indexOf('{');
        if (0 > i) return Optional.empty();
        final var substring = withoutEnd.substring(0, i);
        final var substring1 = withoutEnd.substring(i + "{".length());
        return Optional.of(
                Main.compileClassHeaderOrPlaceholder(substring) + "{" + Main.generatePlaceholder(substring1) + "}" +
                Main.LINE_SEPARATOR);
    }

    private static String compileClassHeaderOrPlaceholder(final String input) {
        return Main.compileClassHeader(input).orElseGet(() -> Main.generatePlaceholder(input));
    }

    private static Optional<String> compileClassHeader(final String input) {
        return Main.createClassHeaderRule().lex(input).map(Main::modifyClassHeader).map(Main::generateClassHeader);
    }

    private static Rule createClassHeaderRule() {
        return new InfixRule(new StringRule("modifiers"), "class ", new StringRule("name"));
    }

    private static Node modifyClassHeader(final Node node) {
        final String newModifiers;
        if ("public".contentEquals(node.findString("modifiers").orElse(""))) newModifiers = "export ";
        else
            newModifiers = "";

        final String name = node.findString("name").orElse("");
        return new MapNode().withString("modifiers", newModifiers).withString("name", name);
    }

    private static String generateClassHeader(final Node node) {
        return Main.createClassHeaderRule().generate(node).orElse("");
    }

    private static List<String> divide(final CharSequence input) {
        Tuple<Boolean, DivideState> current = new Tuple<>(true, new MutableDivideState(input));
        while (current.left()) current = Main.foldAsTuple(current);

        return current.right().advance().stream().toList();
    }

    private static Tuple<Boolean, DivideState> foldAsTuple(final Tuple<Boolean, DivideState> current) {
        final var currentState = current.right();
        final var maybePopped = currentState.pop();
        if (maybePopped.isEmpty()) return new Tuple<>(false, currentState);

        final var popped = maybePopped.get();
        return new Tuple<>(true, Main.foldDecorated(popped.left(), popped.right()));
    }

    private static DivideState foldDecorated(final DivideState state, final char c) {
        return Main.foldSingleQuotes(state, c).orElseGet(() -> Main.foldStatement(state, c));
    }

    private static Optional<DivideState> foldSingleQuotes(final DivideState state, final char c) {
        if ('\'' != c) return Optional.empty();
        final var append = state.append('\'');
        return append.popAndAppendToTuple()
                     .flatMap(Main::foldEscapeInSingleQuotes)
                     .flatMap(DivideState::popAndAppendToOptional);
    }

    private static Optional<DivideState> foldEscapeInSingleQuotes(final Tuple<DivideState, Character> tuple) {
        if ('\\' == tuple.right()) return tuple.left().popAndAppendToOptional();
        return Optional.of(tuple.left());
    }

    private static DivideState foldStatement(final DivideState state, final char c) {
        final var appended = state.append(c);
        if (';' == c && appended.isLevel()) return appended.advance();
        if ('{' == c) return appended.enter();
        if ('}' == c) return appended.exit();
        return appended;
    }

    private static String generatePlaceholder(final String input) {
        return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
    }
}
