package magma.app.compile.lang;

import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.Rule;

import java.util.List;

import static magma.app.compile.lang.RuleBuilder.Empty;
import static magma.app.compile.lang.RuleBuilder.Last;
import static magma.app.compile.lang.RuleBuilder.NodeList;
import static magma.app.compile.lang.RuleBuilder.Prefix;
import static magma.app.compile.lang.RuleBuilder.String;
import static magma.app.compile.lang.RuleBuilder.Strip;
import static magma.app.compile.lang.RuleBuilder.Suffix;

public class CommonLang {
    public static Rule<NodeWithEverything> createPlantUMLRootRule() {
        return NodeList(List.of(createDependencyRule(), Empty()));
    }

    private static Rule<NodeWithEverything> createDependencyRule() {
        final var parent = String("parent");
        final var child = String("child");
        return Suffix(Last(parent, " --> ", child), "\n");
    }

    public static Rule<NodeWithEverything> createJavaRootRule() {
        return NodeList(List.of(createImportRule(), String("value")));
    }

    private static Rule<NodeWithEverything> createImportRule() {
        final var parent = String("parent");
        final var child = String("child");
        return Strip(Prefix(Suffix(Last(parent, ".", child), ";")));
    }
}
