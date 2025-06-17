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
        return compile(input, new CompileState(location));
    }

    private static StringBuilder compile(CharSequence input, CompileState state) {
        final var segments = divide(input);

        final var output = new StringBuilder();
        for (var segment : segments)
            compileRootSegment(segment, state).ifPresent(obj -> {
                for (var entry : obj.entrySet())
                    output.append(entry.getValue());
            });
        return output;
    }

    private static Optional<Map<CompileState, String>> compileRootSegment(String input, CompileState state) {
        final var strip = input.strip();
        if (strip.startsWith("import ")) {
            final var withoutStart = strip.substring("import ".length());
            if (withoutStart.endsWith(";")) {
                final var withoutEnd = withoutStart.substring(0, withoutStart.length() - ";".length());
                final var separator = withoutEnd.lastIndexOf(".");
                final var parent = withoutEnd.substring(0, separator);
                final var child = withoutEnd.substring(separator + ".".length());
                return Optional.of(Map.of(state.addImport(new Location(parent, child)),
                        state.current()
                                .join() + " --> " + withoutEnd + "\n"));
            }
        }

        final var separator = strip.indexOf("{");
        if (separator >= 0) {
            final var beforeContent = strip.substring(0, separator);
            final var maybeStructure = compileStructureDefinition("class",
                    "class",
                    beforeContent,
                    state).or(() -> compileStructureDefinition("interface", "interface", beforeContent, state))
                    .or(() -> compileStructureDefinition("record", "class", beforeContent, state));
            if (maybeStructure.isPresent())
                return maybeStructure;
        }

        return Optional.empty();
    }

    private static Optional<Map<CompileState, String>> compileStructureDefinition(String type, String type1, String input, CompileState state) {
        final var index = input.indexOf(type + " ");
        if (index >= 0) {
            final var afterKeyword = input.substring((type + " ").length() + index);
            return compileStructureDefinitionTruncated(type1, afterKeyword, state);
        }
        return Optional.empty();
    }

    private static Optional<Map<CompileState, String>> compileStructureDefinitionTruncated(String type, String afterKeyword, CompileState state) {
        final var index = afterKeyword.indexOf("implements ");
        if (index >= 0) {
            final var childName = afterKeyword.substring(index + "implements ".length())
                    .strip();

            final var actual = find(state.imports(), childName).orElse(state.current()
                    .resolveSibling(childName));
            return generate(type, state, List.of(actual.join()));
        }
        else
            return generate(type, state, Collections.emptyList());
    }

    private static Optional<Location> find(List<Location> imports, String name) {
        for (var anImport : imports)
            if (anImport.isNamed(name))
                return Optional.of(anImport);

        return Optional.empty();
    }

    private static Optional<Map<CompileState, String>> generate(String type, CompileState state, List<String> superTypes) {
        final var buffer = new StringBuilder();
        for (var superType : superTypes)
            buffer.append(state.current()
                            .join())
                    .append(" --|> ")
                    .append(superType)
                    .append("\n");

        final var generated = type + " " + state.current()
                .join() + "\n" + buffer;

        return Optional.of(Map.of(state, generated));
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
