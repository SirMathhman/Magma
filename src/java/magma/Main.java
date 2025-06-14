package magma;

import magma.app.State;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        try (var stream = Files.walk(Paths.get(".", "src", "java"))) {
            final var sources = stream.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".java")).collect(Collectors.toSet());

            final var output = compileSources(sources);
            final var target = Paths.get(".", "diagram.puml");
            Files.writeString(target, output);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compileSources(Set<Path> sources) throws IOException {
        final var output1 = new StringBuilder();
        for (var source : sources) {
            output1.append(compileSource(source));
        }

        return "@startuml\nskinparam linetype ortho\n" + output1 + "@enduml";
    }

    private static String compileSource(Path source) throws IOException {
        final var input = Files.readString(source);
        final var fileName = source.getFileName().toString();
        final var name = fileName.substring(0, fileName.lastIndexOf("."));
        return "class " + name + "\n" + compileInput(input, name);
    }

    private static String compileInput(String input, String name) {
        final var segments = divide(input);

        final var output = new StringBuilder();
        for (var segment : segments) output.append(compileImport(name, segment).orElse(""));

        return output.toString();
    }

    private static Optional<String> compileImport(String name, String input) {
        final var stripped = input.strip();
        if (!stripped.startsWith("import ")) return Optional.empty();

        final var substring = stripped.substring("import ".length());
        if (!substring.endsWith(";")) return Optional.empty();

        final var substring1 = substring.substring(0, substring.length() - ";".length());
        final var index = substring1.lastIndexOf(".");
        if (index < 0) return Optional.empty();

        final var substring2 = substring1.substring(index + 1);
        return Optional.of(name + " --> " + substring2 + "\n");
    }

    private static List<String> divide(String input) {
        var current = new State();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance().segments();
    }

    private static State fold(State state, char c) {
        final var appended = state.append(c);
        if (c == ';') return appended.advance();
        return appended;
    }
}