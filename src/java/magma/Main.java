package magma;

import magma.io.Location;
import magma.io.Source;
import magma.io.Sources;
import magma.state.MutableState;
import magma.state.State;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
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

        List<Location> imports = new ArrayList<>();
        final var output = new StringBuilder();
        for (var segment : segments)
            compileRootSegment(segment, imports, location).ifPresent(obj -> {
                for (var entry : obj.entrySet()) {
                    imports.add(entry.getKey());
                    output.append(entry.getValue());
                }
            });
        return output;
    }

    private static Optional<Map<Location, String>> compileRootSegment(String input, List<Location> imports, Location location) {
        final var strip = input.strip();
        if (strip.startsWith("import ")) {
            final var withoutStart = strip.substring("import ".length());
            if (withoutStart.endsWith(";")) {
                final var withoutEnd = withoutStart.substring(0, withoutStart.length() - ";".length());
                final var separator = withoutEnd.lastIndexOf(".");
                final var parent = withoutEnd.substring(0, separator);
                final var child = withoutEnd.substring(separator + ".".length());
                final var parent1 = new Location(parent, child);
                final var generated = Map.of(parent1, location.join() + " --> " + withoutEnd + "\n");
                return Optional.of(generated);
            }
        }

        final var separator = strip.indexOf("{");
        if (separator >= 0) {
            final var beforeContent = strip.substring(0, separator);
            final var or = compileStructureDefinition("class",
                    "class",
                    imports,
                    location, beforeContent).or(() -> compileStructureDefinition("interface", "interface",
                            imports, location, beforeContent))
                    .or(() -> compileStructureDefinition("record", "class", imports, location, beforeContent));

            if (or.isPresent())
                return or;
        }

        return Optional.empty();
    }

    private static Optional<Map<Location, String>> compileStructureDefinition(String type, String type1, List<Location> imports, Location location, String input) {
        final var index = input.indexOf(type + " ");
        if (index >= 0) {
            final var afterKeyword = input.substring((type + " ").length() + index);
            return compileStructureDefinitionTruncated(type1, afterKeyword, imports, location);
        }
        return Optional.empty();
    }

    private static Optional<Map<Location, String>> compileStructureDefinitionTruncated(String type, String afterKeyword, List<Location> imports, Location location) {
        final var index = afterKeyword.indexOf("implements ");
        if (index >= 0) {
            final var childName = afterKeyword.substring(index + "implements ".length())
                    .strip();

            final var actual = find(imports, childName).orElse(location);
            return generate(type, location, List.of(actual.join()));
        }
        else
            return generate(type, location, Collections.emptyList());
    }

    private static Optional<Location> find(List<Location> imports, String name) {
        for (var anImport : imports)
            if (anImport.isNamed(name)) {
                return Optional.of(anImport);
            }

        return Optional.empty();
    }

    private static Optional<Map<Location, String>> generate(String type, Location location, List<String> superTypes) {
        final var buffer = new StringBuilder();
        for (var superType : superTypes)
            buffer.append(location.namespace())
                    .append(".")
                    .append(location.name())
                    .append(" --|> ")
                    .append(superType)
                    .append("\n");

        return Optional.of(Map.of(location, type + " " + location.namespace() + "." + location.name() + "\n" + buffer));
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
