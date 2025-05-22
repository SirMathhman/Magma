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
        if (';' == c && state.isLevel()) {
            return current.advance();
        }
        if(c == '{') return current.enter();
        if(c == '}') return current.exit();
        return current;
    }

    private static String compileRootSegment(String input) {
        var stripped = input.strip();
        if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
            return "";
        }

        var classIndex = stripped.indexOf("class ");
        if (0 <= classIndex) {
            var left = stripped.substring(0, classIndex);
            var right = stripped.substring(classIndex + "class ".length());
            var contentStart = right.indexOf("{");
            if (0 <= contentStart) {
                var beforeContent = right.substring(0, contentStart);
                var withEnd = right.substring(contentStart + "{".length()).strip();
                if (withEnd.endsWith("}")) {
                    var content = withEnd.substring(0, withEnd.length() - 1);
                    return Main.generatePlaceholder(left) + "class " + beforeContent + "{" + content + "}";
                }
            }
        }

        return Main.generatePlaceholder(stripped);
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
