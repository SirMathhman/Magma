package magma;

import magma.app.LastRule;
import magma.app.MutableState;
import magma.app.State;
import magma.app.StringRule;
import magma.app.node.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Main {
    private static final String SEPARATOR = System.lineSeparator();

    private Main() {
    }

    public static void main(final String[] args) {
        final var sourceRoot = Paths.get(".", "src", "java");
        try (final var files = Files.walk(sourceRoot)) {
            final Collector<Path, ?, Set<Path>> toSet = Collectors.toSet();
            final var sources = files.filter(Files::isRegularFile)
                    .filter(path -> {
                        final var pathAsString = path.toString();
                        return pathAsString.endsWith(".java");
                    })
                    .collect(toSet);

            final StringBuilder output = new StringBuilder();
            for (final var source : sources) {
                final var fileName = source.getFileName()
                        .toString();
                final var separator = fileName.lastIndexOf('.');
                final var name = fileName.substring(0, separator);

                final var input = Files.readString(source);
                final var compiled = Main.compile(input, name);

                output.append("class ")
                        .append(name)
                        .append(Main.SEPARATOR)
                        .append(compiled);
            }

            final var target = Paths.get(".", "diagram.puml");
            final var joined = String.join(Main.SEPARATOR, "@startuml", "skinparam linetype ortho", output, "@enduml");
            Files.writeString(target, joined);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(final CharSequence input, final String source) {
        final var segments = Main.divide(input);

        final var outputBuilder = new StringBuilder();
        for (final var segment : segments)
            Main.compileRootSegment(segment, source)
                    .ifPresent(outputBuilder::append);

        return outputBuilder.toString();
    }

    private static Optional<String> compileRootSegment(final String segment, final String source) {
        final var strip = segment.strip();
        if (!strip.startsWith("import "))
            return Optional.empty();

        final var prefixLength = "import ".length();
        final var withoutPrefix = strip.substring(prefixLength);
        final var inputLength0 = withoutPrefix.length();
        if (withoutPrefix.isEmpty() || ';' != withoutPrefix.charAt(inputLength0 - 1))
            return Optional.empty();

        final var inputLength = withoutPrefix.length();
        final var suffixLength = ";".length();
        final var withoutEnd = withoutPrefix.substring(0, inputLength - suffixLength);

        return new LastRule(".", new StringRule("destination")).lex(withoutEnd)
                .flatMap(node -> Main.generate(node.withString("source", source)));
    }

    private static Optional<String> generate(final Node node) {
        final var source = node.findString("source")
                .orElse("");
        final var destination = node.findString("destination")
                .orElse("");
        return Optional.of(source + " --> " + destination + Main.SEPARATOR);
    }

    private static List<String> divide(final CharSequence input) {
        final State state = new MutableState();
        final var length = input.length();
        var current = state;
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = Main.fold(current, c);
        }

        return current.advance()
                .segments();
    }

    private static State fold(final State state, final char c) {
        final var appended = state.append(c);
        if (';' == c)
            return appended.advance();
        return appended;
    }
}
