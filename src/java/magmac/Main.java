package magmac;

import magmac.compile.InfixRule;
import magmac.compile.MapNode;
import magmac.compile.State;
import magmac.compile.StringRule;
import magmac.compile.SuffixRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
        if ('{' == c) {
            return current.enter();
        }
        if ('}' == c) {
            return current.exit();
        }
        return current;
    }

    private static String compileRootSegment(String input) {
        var stripped = input.strip();
        if (stripped.startsWith("package ") || stripped.startsWith("import ")) {
            return "";
        }

        return Main.createClassRule()
                .apply(stripped)
                .flatMap(node -> Main.generateClass(node))
                .orElseGet(() -> Main.generatePlaceholder(stripped));
    }

    private static InfixRule createClassRule() {
        var afterKeyword = new InfixRule(new StringRule("before-content"), "{", new SuffixRule("}", new StringRule("content")));
        return new InfixRule(new StringRule("before-content"), "class ", afterKeyword);
    }

    private static Optional<String> generateClass(MapNode node) {
        var beforeKeyword = node.findStringOrEmpty("before-keyword");
        var beforeContent = node.findStringOrEmpty("before-content");
        var content = node.findStringOrEmpty("content");
        return Optional.of(Main.generatePlaceholder(beforeKeyword) + "class " + beforeContent + "{" + content + "}");
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
