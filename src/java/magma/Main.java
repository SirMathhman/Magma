package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        final var sourceDirectory = Paths.get(".", "src", "java");
        try (final var stream = Files.walk(sourceDirectory)) {
            final var sources = stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString()
                            .endsWith(".java"))
                    .collect(Collectors.toSet());

            final var builder = new StringBuilder();
            for (var source : sources)
                builder.append(compileSource(source, sourceDirectory));

            final var path = Paths.get(".", "diagram.puml");
            Files.writeString(path, "@startuml\nskinparam linetype ortho\n" + builder + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static StringBuilder compileSource(Path source, Path sourceDirectory) throws IOException {
        final var relative = sourceDirectory.relativize(source);
        final var relativeParent = relative.getParent();

        final var input = Files.readString(source);
        final var segments = computeNamespace(relativeParent);

        final var fileName = source.getFileName()
                .toString();
        final var separator = fileName.lastIndexOf(".");
        final var name = fileName.substring(0, separator);

        final var joined = String.join(".", segments);
        return compile(input, joined + "." + name);
    }

    private static List<String> computeNamespace(Path parent) {
        final List<String> segments = new ArrayList<>();
        for (var i = 0; i < parent.getNameCount(); i++)
            segments.add(parent.getName(i)
                    .toString());
        return segments;
    }

    private static StringBuilder compile(CharSequence input, String name) {
        final var segments = divide(input);

        final var output = new StringBuilder();
        for (var segment : segments)
            compileRootSegment(segment, name).ifPresent(output::append);
        return output;
    }

    private static Optional<String> compileRootSegment(String input, String name) {
        final var strip = input.strip();
        if (strip.startsWith("import ")) {
            final var withoutStart = strip.substring("import ".length());
            if (withoutStart.endsWith(";")) {
                final var withoutEnd = withoutStart.substring(0, withoutStart.length() - ";".length());
                return Optional.of(name + " --> " + withoutEnd + "\n");
            }
        }
        if (strip.contains("class "))
            return Optional.of("class " + name + "\n");
        return Optional.empty();
    }

    private static List<String> divide(CharSequence input) {
        State current = new MutableState();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance()
                .unwrap();
    }

    private static State fold(State state, char c) {
        final var appended = state.append(c);
        if (c == ';' && appended.isLevel())
            return appended.advance();
        else {
            if (c == '{')
                return appended.enter();
            if (c == '}')
                return appended.exit();
        }
        return appended;
    }
}
