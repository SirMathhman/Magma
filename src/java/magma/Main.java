package magma;

import magma.app.Node;
import magma.app.State;

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

            final var output = new StringBuilder();
            for (var source : sources)
                output.append(compileSource(source));

            Files.writeString(Paths.get(".", "diagram.puml"), "@startuml\nskinparam linetype ortho\n" + output + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compileSource(Path source) throws IOException {
        final var fileName = source.getFileName()
                .toString();

        final var extensionSeparator = fileName.lastIndexOf(".");
        final var name = fileName.substring(0, extensionSeparator);

        final var output = new StringBuilder();
        final var segments = divide(Files.readString(source));
        for (var segment : segments) {
            final var extracted = compileImport(segment, name);
            extracted.ifPresent(output::append);
        }

        return output.append("class ")
                .append(name)
                .append("\n")
                .toString();
    }

    private static Optional<String> compileImport(String input, String name) {
        final var stripped = input.strip();
        return getStringOptional(name, stripped);
    }

    private static Optional<String> getStringOptional(String name, String stripped) {
        if (!stripped.startsWith("import "))
            return Optional.empty();

        final var withoutPrefix = stripped.substring("import ".length());
        return getOptionalString(name, withoutPrefix);
    }

    private static Optional<String> getOptionalString(String name, String withoutPrefix) {
        if (!withoutPrefix.endsWith(";"))
            return Optional.empty();

        final var withoutSuffix = withoutPrefix.substring(0, withoutPrefix.length() - ";".length());
        return getString(name, withoutSuffix);
    }

    private static Optional<String> getString(String name, String withoutSuffix) {
        final var separator = withoutSuffix.lastIndexOf(".");
        if (separator < 0)
            return Optional.empty();

        final var child = withoutSuffix.substring(separator + ".".length());
        return Optional.of(generate(new Node().withString("parent", name)
                .withString("child", child)));
    }

    private static String generate(Node node) {
        return node.findString("parent")
                .orElse("") + " --> " + node.findString("child")
                .orElse("") + "\n";
    }

    private static List<String> divide(CharSequence input) {
        var current = new State();
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
}
