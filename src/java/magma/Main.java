package magma;

import magma.state.MutableState;
import magma.state.State;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try (final var stream = Files.walk(Paths.get(".", "src", "java"))) {
            final var sources = stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString()
                            .endsWith(".java"))
                    .collect(Collectors.toSet());

            final var output = compileAll(sources);
            Files.writeString(Paths.get(".", "diagram.puml"),
                    "@startuml\nskinparam linetype ortho\n" + output + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compileAll(Iterable<Path> sources) throws IOException {
        final StringBuilder output = new StringBuilder();
        for (var source : sources) {
            final var input = Files.readString(source);
            final var segments = divide(input);

            final var fileName = source.getFileName()
                    .toString();
            final var separator = fileName.lastIndexOf(".");
            final var name = fileName.substring(0, separator);

            output.append("class ")
                    .append(name)
                    .append("\n")
                    .append(compileRootSegments(segments, name));
        }

        return output.toString();
    }

    private static List<String> divide(CharSequence input) {
        State current = new MutableState();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance()
                .segments();
    }

    private static State fold(State current, char c) {
        final var appended = current.append(c);
        if (c == ';' && appended.isLevel())
            return appended.advance();
        if (c == '{')
            return appended.enter();
        if (c == '}')
            return appended.exit();
        return appended;
    }

    private static String compileRootSegments(Iterable<String> segments, String name) {
        final var output = new StringBuilder();
        for (var segment : segments)
            compileRootSegment(segment.strip(), name).ifPresent(output::append);

        return output.toString();
    }

    private static Optional<String> compileRootSegment(String input, String source) {
        return compileImport(input, source).or(() -> {
            final var contentStart = input.indexOf("{");
            if (contentStart >= 0)
                return Optional.of(input.substring(0, contentStart)
                        .strip() + "\n");
            else
                return Optional.empty();
        });
    }

    private static Optional<String> compileImport(String input, String source) {
        if (!input.startsWith("import "))
            return Optional.empty();

        final var withoutPrefix = input.substring("import ".length());
        if (!withoutPrefix.endsWith(";"))
            return Optional.empty();

        final var withoutEnd = withoutPrefix.substring(0, withoutPrefix.length() - ";".length());
        final var separator = withoutEnd.lastIndexOf(".");
        if (separator < 0)
            return Optional.empty();

        final var destination = withoutEnd.substring(separator + 1);
        return Optional.of(source + " --> " + destination + "\n");
    }
}
