package magmac;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Main {
    public static void main() {
        try {
            var source = Paths.get(".", "src", "java", "magmac", "Main.java");
            var diagramPath = Paths.get(".", "diagram.puml");

            var input = Files.readString(source);
            var output = Main.compile(input);

            var umlContent = "@startuml\n" + output + "\n@enduml";
            Files.writeString(diagramPath, umlContent);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(String input) {
        var segments = Main.divide(input, new State());

        var output = new StringBuilder();
        for (var segment : segments) {
            output.append(Main.compileRootSegment(segment));
        }

        return output.toString();
    }

    private static List<String> divide(String input, State state) {
        var current = state;
        var length = input.length();
        for (var i = 0; i < length; i++) {
            var c = input.charAt(i);
            current = Main.fold(current, c);
        }

        return current.advance().segments();
    }

    private static State fold(State state, char c) {
        var current = state.append(c);
        if (';' == c) {
            return current.advance();
        }
        return current;
    }

    private static String compileRootSegment(String segment) {
        if (segment.startsWith("package ")) {
            return "";
        }

        return Main.generatePlaceholder(segment.strip());
    }

    private static String generatePlaceholder(String input) {
        return Arrays.stream(Main.splitIntoCommentSegments(input))
                .filter(value -> !value.isEmpty())
                .map(value -> "'" + value + "\n")
                .collect(Collectors.joining());
    }

    private static String[] splitIntoCommentSegments(String input) {
        return input.replace("@startuml", "startuml")
                .replace("@enduml", "enduml")
                .split(Pattern.quote("\n"));
    }
}
