package magma.app.compile.lang;

import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.Rule;

import java.util.List;

public class CommonLang {
    public static Rule<NodeWithEverything> createPlantUMLRootRule() {
        return RuleBuilder.NodeList(List.of(createDependencyRule(), RuleBuilder.Empty()));
    }

    private static Rule<NodeWithEverything> createDependencyRule() {
        final var parent = RuleBuilder.String("parent");
        final var child = RuleBuilder.String("child");
        return RuleBuilder.Suffix(RuleBuilder.Last(parent, " --> ", child), "\n");
    }

    public static Rule<NodeWithEverything> createJavaRootRule() {
        return RuleBuilder.NodeList(List.of(createImportRule(), RuleBuilder.String("value")));
    }

    private static Rule<NodeWithEverything> createImportRule() {
        final var parent = RuleBuilder.String("parent");
        final var child = RuleBuilder.String("child");
        return RuleBuilder.Strip(RuleBuilder.Prefix(RuleBuilder.Suffix(RuleBuilder.Last(parent, ".", child), ";")));
    }
}
