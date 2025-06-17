package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        final var sourceDirectory = Paths.get(".", "src", "java");
        try (var files = Files.walk(sourceDirectory)) {
            final var sources = files.filter(Files::isRegularFile)
                    .filter(path -> path.toString()
                            .endsWith("java"))
                    .toList();

            final var stringBuilder = new StringBuilder();
            for (var source : sources)
                stringBuilder.append(compileSource(sourceDirectory, source));

            final var target = Paths.get(".", "diagram.puml");
            Files.writeString(target, "@startuml\nskinparam linetype ortho\n" + stringBuilder + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compileSource(Path sourceDirectory, Path source) throws IOException {
        final var relativeParent = sourceDirectory.relativize(source)
                .getParent();

        final Collection<String> namespace = new ArrayList<>();
        for (var i = 0; i < relativeParent.getNameCount(); i++)
            namespace.add(relativeParent.getName(i)
                    .toString());

        final var fileName = source.getFileName()
                .toString();
        final var separator = fileName.lastIndexOf(".");
        final var name = fileName.substring(0, separator);

        final var joined = String.join(".", namespace);
        final var input = Files.readString(source);
        final var segments = divide(input);
        final var joinedName = joined + "." + name;
        final var output = compileSegments(segments, joinedName);
        return "class " + joinedName + "\n" + output;
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

    private static State fold(State state, char c) {
        final var appended = state.append(c);
        if (c == ';')
            return appended.advance();
        return appended;
    }

    private static StringBuilder compileSegments(Iterable<String> segments, String name) {
        final var output = new StringBuilder();
        for (var segment : segments)
            compileImport(segment, name).ifPresent(output::append);
        return output;
    }

    private static Optional<String> compileImport(String segment, String name) {
        final var stripped = segment.strip();
        if (!stripped.startsWith("import "))
            return Optional.empty();

        final var withoutStart = stripped.substring("import ".length());
        return getString(withoutStart).map(node -> node.withString("source", name))
                .flatMap(Main::generate);
    }

    private static Optional<Node> getString(String withoutStart) {
        if (!withoutStart.endsWith(";"))
            return Optional.empty();

        final var withoutEnd = withoutStart.substring(0, withoutStart.length() - ";".length());
        final var node = new MapNode().withString("destination", withoutEnd);
        return Optional.of(node);
    }

    private static Optional<String> generate(Node node) {
        return Optional.of(node.findString("source")
                .orElse("") + " --> " + node.findString("destination")
                .orElse("") + "\n");
    }
}
