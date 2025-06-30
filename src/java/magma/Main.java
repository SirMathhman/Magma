package magma;

import magma.api.Tuple;
import magma.divide.DivideState;
import magma.divide.MutableDivideState;
import magma.node.MapNode;
import magma.node.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private Main() {}

    public static void main(final String[] args) {
        final var root = Paths.get(".", "src", "java");
        try (final var stream = Files.walk(root)) {
            final Collector<Path, ?, Set<Path>> setCollector = Collectors.toSet();
            final var files = stream.filter(Files::isRegularFile).filter(Main::isJavaFile).collect(setCollector);

            final var outputRootSegments = Main.runWithSources(files);
            final var target = Paths.get(".", "diagram.puml");
            final var joined = String.join(Main.LINE_SEPARATOR, outputRootSegments);
            Files.writeString(target, joined);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static boolean isJavaFile(final Path file) {
        final var asString = file.toString();
        return asString.endsWith(".java");
    }

    private static Collection<String> runWithSources(final Iterable<Path> files) throws IOException {
        final var pre = List.of("@startuml", "skinparam linetype ortho");
        final Collection<String> outputRootSegments = new ArrayList<>(pre);
        for (final var source : files) {
            final var input = Files.readString(source);

            final var fileName = source.getFileName().toString();
            final var separator = fileName.lastIndexOf('.');
            final var parent = fileName.substring(0, separator);

            final var output = Main.compile(input, parent);
            outputRootSegments.add("class " + parent);
            outputRootSegments.addAll(output);
        }

        outputRootSegments.add("@enduml");
        return outputRootSegments;
    }

    private static List<String> compile(final CharSequence input, final String parent) {
        final var segments = Main.divide(input).toList();
        final List<String> output = new ArrayList<>();
        for (final var segment : segments) Main.compileRootSegment(segment, parent).ifPresent(output::add);
        return output;
    }

    private static Optional<String> compileRootSegment(final String input, final String parent) {
        return Main.compileImport(input, parent).or(() -> Main.compileClass(input));
    }

    private static Optional<String> compileClass(final String input) {
        final var strip = input.strip();
        final var stripLength = strip.length();
        if (strip.isEmpty() || '}' != strip.charAt(stripLength - 1)) return Optional.empty();
        final var suffixLength = "}".length();
        final var substring = strip.substring(0, stripLength - suffixLength);

        final var i = substring.indexOf('{');
        if (0 > i) return Optional.empty();
        final var header = substring.substring(0, i);
        final var classIndex = header.indexOf("class ");

        if (0 <= classIndex) {
            final var infixLength = "class ".length();
            final var slice = header.substring(classIndex + infixLength);
            return Optional.of("class " + slice);
        }
        return Optional.empty();
    }

    private static Optional<String> compileImport(final String input, final String parent) {
        final var strip = input.strip();

        final var length = strip.length();
        if (strip.isEmpty() || ';' != strip.charAt(length - 1)) return Optional.empty();
        final var suffixLength = ";".length();
        final var substring = strip.substring(0, length - suffixLength);

        if (!substring.startsWith("import ")) return Optional.empty();
        final var prefixLength = "import ".length();

        final var substring1 = substring.substring(prefixLength);
        final var separator = substring1.lastIndexOf('.');
        if (0 <= separator) {
            final var infixLength = ".".length();
            final var child = substring1.substring(separator + infixLength);
            final var node = new MapNode().withString("parent", parent).withString("child", child);

            final var generated = Main.generate(node);
            return Optional.of(generated);
        }

        return Optional.empty();
    }

    private static String generate(final Node node) {
        return node.find("parent").orElse("") + " <-- " + node.find("child").orElse("");
    }

    private static Stream<String> divide(final CharSequence input) {
        var current = new Tuple<>(true, (DivideState) new MutableDivideState(input));
        while (current.left()) {
            final var right = current.right();
            current = Main.fold(right);
        }

        return current.right().advance().stream();
    }

    private static Tuple<Boolean, DivideState> fold(final DivideState state) {
        final var maybeNextTuple = state.pop();
        if (maybeNextTuple.isEmpty()) return new Tuple<>(false, state);

        final var nextTuple = maybeNextTuple.get();
        final var nextState = nextTuple.left();
        final var next = nextTuple.right();

        final var folded = Main.fold(nextState, next);
        return new Tuple<>(true, folded);
    }

    private static DivideState fold(final DivideState current, final char next) {
        final var appended = current.append(next);
        if (';' == next && appended.isLevel()) return appended.advance();
        if ('{' == next) return appended.enter();
        if ('}' == next) return appended.exit();
        return appended;
    }
}
