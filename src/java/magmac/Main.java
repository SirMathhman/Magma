package magmac;

import magmac.compile.DivideRule;
import magmac.compile.InfixRule;
import magmac.compile.OrRule;
import magmac.compile.PrefixRule;
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
        return Main.createJavaRootRule().parse(input)
                .flatMap(node -> Main.createPlantUMLRootRule().generate(node))
                .orElse("");
    }

    private static DivideRule createPlantUMLRootRule() {
        return new DivideRule("children", new OrRule(List.of(
                Main.createPlantUMLClassRule()
        )));
    }

    private static InfixRule createPlantUMLClassRule() {
        var afterKeyword = new SuffixRule(new StringRule("before-content"), "{\n}\n");
        return new InfixRule(new StringRule("before-keyword"), "class ", afterKeyword);
    }

    private static DivideRule createJavaRootRule() {
        return new DivideRule("children", Main.createJavaRootSegmentRule());
    }

    private static OrRule createJavaRootSegmentRule() {
        return new OrRule(List.of(
                Main.createNamespacedRule(),
                Main.createJavaClassRule()
        ));
    }

    private static StripRule createNamespacedRule() {
        return new StripRule(new OrRule(List.of(
                new PrefixRule("package ", new StringRule("after")),
                new PrefixRule("import ", new StringRule("after"))
        )));
    }

    private static InfixRule createJavaClassRule() {
        var afterKeyword = new InfixRule(new StringRule("before-content"), "{", new SuffixRule(new StringRule("content"), "}"));
        return new InfixRule(new StringRule("before-keyword"), "class ", afterKeyword);
    }
}
