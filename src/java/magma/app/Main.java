package magma.app;

import magma.api.Tuple;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.io.location.SimpleLocation;
import magma.app.io.source.PathSource;
import magma.app.io.source.Source;
import magma.app.io.source.Sources;
import magma.app.state.MutableState;
import magma.app.state.State;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        run().ifPresent(Throwable::printStackTrace);
    }

    private static Optional<IOException> run() {
        return switch (new Sources(Paths.get(".", "src", "java")).collect()) {
            case Err<Set<PathSource>, IOException>(var error) -> Optional.of(error);
            case Ok<Set<PathSource>, IOException>(var files) -> compileAll(files);
        };
    }

    private static Optional<IOException> compileAll(Iterable<PathSource> files) {
        final var builder = new StringBuilder();
        for (var source : files) {
            final var compiled = compileSource(source);
            switch (compiled) {
                case Err<String, IOException>(var error) -> {
                    return Optional.of(error);
                }
                case Ok<String, IOException>(var value) -> builder.append(value);
            }
        }

        final var path = Paths.get(".", "diagram.puml");
        final var output = "@startuml\nskinparam linetype ortho\n" + builder + "@enduml";
        return writeString(path, output);
    }

    private static Optional<IOException> writeString(Path path, String output) {
        try {
            Files.writeString(path, output);
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(e);
        }
    }

    private static Result<String, IOException> compileSource(Source source) {
        final var location = source.computeLocation();
        final var input = source.readString();
        return switch (input) {
            case Err<String, IOException>(var error) -> new Err<>(error);
            case Ok<String, IOException>(var input0) -> new Ok<>(compile(new SimpleCompileState(location), input0));
        };
    }

    private static String compile(CompileState state, CharSequence input) {
        final var segments = divide(input);

        var current = state;
        final var output = new StringBuilder();
        for (var segment : segments) {
            final var maybeCompiled = compileRootSegment(segment, current);
            if (maybeCompiled.isPresent()) {
                final var compiled = maybeCompiled.get();
                current = compiled.left();
                output.append(compiled.right());
            }
        }
        return output.toString();
    }

    private static Optional<Tuple<CompileState, String>> compileRootSegment(String input, CompileState state) {
        final var strip = input.strip();
        if (strip.startsWith("import ")) {
            final var withoutStart = strip.substring("import ".length());
            if (withoutStart.endsWith(";")) {
                final var withoutEnd = withoutStart.substring(0, withoutStart.length() - ";".length());
                final var separator = withoutEnd.lastIndexOf(".");
                final var parent = withoutEnd.substring(0, separator);
                final var child = withoutEnd.substring(separator + ".".length());
                return Optional.of(new Tuple<>(state.addImport(new SimpleLocation(parent, child)),
                        state.joinLocation() + " --> " + withoutEnd + "\n"));
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

    private static Optional<Tuple<CompileState, String>> compileStructureDefinition(String type, String type1, String input, CompileState state) {
        final var index = input.indexOf(type + " ");
        if (index >= 0) {
            final var afterKeyword = input.substring((type + " ").length() + index);
            return compileStructureDefinitionTruncated(type1, afterKeyword, state);
        }
        return Optional.empty();
    }

    private static Optional<Tuple<CompileState, String>> compileStructureDefinitionTruncated(String type, String afterKeyword, CompileState state) {
        final var index = afterKeyword.indexOf("implements ");
        if (index >= 0) {
            final var childName = afterKeyword.substring(index + "implements ".length())
                    .strip();

            final var separator = childName.indexOf("<");
            final var trimmed = separator == -1 ? childName : childName.substring(0, separator);

            final var actual = state.find(trimmed)
                    .orElse(state.resolveSibling(trimmed));
            return generate(type, state, List.of(actual.join()));
        }
        else
            return generate(type, state, Collections.emptyList());
    }

    private static Optional<Tuple<CompileState, String>> generate(String type, CompileState state, Iterable<String> superTypes) {
        final var buffer = new StringBuilder();
        for (var superType : superTypes)
            buffer.append(state.joinLocation())
                    .append(" --|> ")
                    .append(superType)
                    .append("\n");

        final var generated = type + " " + state.joinLocation() + "\n" + buffer;
        return Optional.of(new Tuple<>(state, generated));
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
