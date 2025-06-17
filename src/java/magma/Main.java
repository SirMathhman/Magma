package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        final var namespace = String.join(".", segments);
        return compile(input, namespace, name);
    }

    private static List<String> computeNamespace(Path parent) {
        final List<String> segments = new ArrayList<>();
        for (var i = 0; i < parent.getNameCount(); i++)
            segments.add(parent.getName(i)
                    .toString());
        return segments;
    }

    private static StringBuilder compile(CharSequence input, String namespace, String name) {
        final var segments = divide(input);

        final var imports = new HashMap<String, String>();
        final var output = new StringBuilder();
        for (var segment : segments)
            compileRootSegment(segment, namespace, name, imports).ifPresent(obj -> {
                for (var entry : obj.entrySet()) {
                    output.append(entry.getKey());
                    imports.putAll(entry.getValue());
                }
            });
        return output;
    }

    private static Optional<Map<String, Map<String, String>>> compileRootSegment(String input, String namespace, String name, Map<String, String> imports) {
        final var strip = input.strip();
        if (strip.startsWith("import ")) {
            final var withoutStart = strip.substring("import ".length());
            if (withoutStart.endsWith(";")) {
                final var withoutEnd = withoutStart.substring(0, withoutStart.length() - ";".length());
                final var separator = withoutEnd.lastIndexOf(".");
                final var parent = withoutEnd.substring(0, separator);
                final var child = withoutEnd.substring(separator + ".".length());
                final var parent1 = Map.of(child, parent);
                final var generated = Map.of(namespace + "." + name + " --> " + withoutEnd + "\n", parent1);
                return Optional.of(generated);
            }
        }

        final var separator = strip.indexOf("{");
        if (separator >= 0) {
            final var beforeContent = strip.substring(0, separator);
            final var classIndex = beforeContent.indexOf("class ");
            if (classIndex >= 0) {
                final var afterKeyword = beforeContent.substring("class ".length() + classIndex);
                return compileStructure("class", namespace, afterKeyword, imports);
            }

            final var interfaceIndex = beforeContent.indexOf("interface ");
            if (interfaceIndex >= 0) {
                final var afterKeyword = beforeContent.substring("interface ".length() + interfaceIndex);
                return compileStructure("interface", namespace, afterKeyword, imports);
            }
        }

        return Optional.empty();
    }

    private static Optional<Map<String, Map<String, String>>> compileStructure(String type, String namespace, String afterKeyword, Map<String, String> imports) {
        final var index = afterKeyword.indexOf("implements ");
        if (index >= 0) {
            final var substring = afterKeyword.substring(0, index);
            final var substring1 = afterKeyword.substring(index + "implements ".length())
                    .strip();

            final var superTypeNamespace = imports.getOrDefault(substring1, namespace);
            return generate(type, namespace, substring, List.of(superTypeNamespace + "." + substring1));
        }
        else
            return generate(type, namespace, afterKeyword, Collections.emptyList());
    }

    private static Optional<Map<String, Map<String, String>>> generate(String type, String namespace, String name, List<String> superTypes) {
        final var buffer = new StringBuilder();
        for (var superType : superTypes)
            buffer.append(namespace + "." + name + " --|> " + superType + "\n");

        return Optional.of(Map.of(type + " " + namespace + "." + name + "\n" + buffer, Collections.emptyMap()));
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
