package magma;

import magma.io.Location;
import magma.io.Source;
import magma.io.Sources;
import magma.state.MutableState;
import magma.state.State;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        try {
            final var sources = new Sources(Paths.get(".", "src", "java")).collect();
            final var builder = new StringBuilder();
            for (var source : sources)
                builder.append(compileSource(source));

            final var path = Paths.get(".", "diagram.puml");
            Files.writeString(path, "@startuml\nskinparam linetype ortho\n" + builder + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static StringBuilder compileSource(Source source) throws IOException {
        final var location = source.computeLocation();
        final var input = source.readString();
        return compile(input, location);
    }

    private static StringBuilder compile(CharSequence input, Location location) {
        final var segments = divide(input);

        final Map<String, String> imports = new HashMap<>();
        final var output = new StringBuilder();
        for (var segment : segments)
            compileRootSegment(segment, imports, location).ifPresent(obj -> {
                for (var entry : obj.entrySet()) {
                    output.append(entry.getKey());
                    imports.putAll(entry.getValue());
                }
            });
        return output;
    }

    private static Optional<Map<String, Map<String, String>>> compileRootSegment(String input, Map<String, String> imports, Location location) {
        final var namespace = location.namespace();
        final var name = location.name();

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
            final var or = compileStructureDefinition("class", "class", imports, location, beforeContent, namespace).or(
                            () -> compileStructureDefinition("interface",
                                    "interface",
                                    imports,
                                    location,
                                    beforeContent,
                                    namespace))
                    .or(() -> compileStructureDefinition("record",
                            "class",
                            imports,
                            location,
                            beforeContent,
                            namespace));

            if (or.isPresent())
                return or;
        }

        return Optional.empty();
    }

    private static Optional<Map<String, Map<String, String>>> compileStructureDefinition(String type, String type1, Map<String, String> imports, Location location, String input, String namespace) {
        final var index = input.indexOf(type + " ");
        if (index >= 0) {
            final var afterKeyword = input.substring((type + " ").length() + index);
            return compileStructureDefinitionTruncated(type1, namespace, afterKeyword, imports, location);
        }
        return Optional.empty();
    }

    private static Optional<Map<String, Map<String, String>>> compileStructureDefinitionTruncated(String type, String namespace, String afterKeyword, Map<String, String> imports, Location location) {
        final var index = afterKeyword.indexOf("implements ");
        if (index >= 0) {
            final var substring1 = afterKeyword.substring(index + "implements ".length())
                    .strip();

            final var superTypeNamespace = imports.getOrDefault(substring1, namespace);
            return generate(type, location, List.of(superTypeNamespace + "." + substring1));
        }
        else
            return generate(type, location, Collections.emptyList());
    }

    private static Optional<Map<String, Map<String, String>>> generate(String type, Location location, List<String> superTypes) {
        final var buffer = new StringBuilder();
        for (var superType : superTypes)
            buffer.append(location.namespace())
                    .append(".")
                    .append(location.name())
                    .append(" --|> ")
                    .append(superType)
                    .append("\n");

        return Optional.of(Map.of(type + " " + location.namespace() + "." + location.name() + "\n" + buffer,
                Collections.emptyMap()));
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
