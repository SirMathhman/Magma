package magma;

import magma.app.Result;
import magma.app.State;
import magma.app.result.EmptyResult;
import magma.app.result.PresentResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try (final var stream = Files.walk(Paths.get(".", "src", "java"))) {
            final var sources = stream.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".java")).toList();

            final var output = new StringBuilder();
            for (var source : sources) {
                final var fileName = source.getFileName().toString();
                final var separator = fileName.lastIndexOf(".");
                final var name = fileName.substring(0, separator);
                output.append("class " + name + "\n");

                final var input = Files.readString(source);
                final var result = compile(input, name);

                output.append(result);
            }

            final var target = Paths.get(".", "diagram.puml");
            Files.writeString(target, "@startuml\nskinparam linetype ortho\n" + output + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(String input, String name) throws IOException {
        final var segments = divide(input);
        var output = new StringBuilder();
        for (var segment : segments)
            output = compileRootSegment(name, segment).appendTo(output);

        return output.toString();
    }

    private static Result compileRootSegment(String name, String input) {
        final var strip = input.strip();
        if (!strip.startsWith("import "))
            return new EmptyResult();

        final var substring = strip.substring("import ".length());
        if (!substring.endsWith(";"))
            return new EmptyResult();

        final var substring1 = substring.substring(0, substring.length() - ";".length());
        final var index = substring1.lastIndexOf(".");
        if (index < 0)
            return new EmptyResult();

        final var destination = substring1.substring(index + ".".length());
        return new PresentResult(name + " --> " + destination + "\n");
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
        if (c == ';')
            return appended.advance();
        return appended;
    }
}
