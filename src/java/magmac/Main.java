package magmac;

import magmac.compile.InfixRule;
import magmac.compile.OrRule;
import magmac.compile.PrefixRule;
import magmac.compile.State;
import magmac.compile.StringRule;
import magmac.compile.StripRule;
import magmac.compile.SuffixRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
        return Main.getString(input);
    }

    private static String getString(String input) {
        return Main.createRootSegmentRule()
                .parse(input)
                .flatMap(node -> Main.createRootSegmentRule().generate(node))
                .orElse("");
    }

    private static OrRule createRootSegmentRule() {
        return new OrRule(List.of(
                Main.createNamespacedRule(),
                Main.createClassRule()
        ));
    }

    private static StripRule createNamespacedRule() {
        return new StripRule(new OrRule(List.of(
                new PrefixRule("package ", new StringRule("after")),
                new PrefixRule("import ", new StringRule("after"))
        )));
    }

    private static InfixRule createClassRule() {
        var afterKeyword = new InfixRule(new StringRule("before-content"), "{", new SuffixRule(new StringRule("content"), "}"));
        return new InfixRule(new StringRule("before-keyword"), "class ", afterKeyword);
    }
}
