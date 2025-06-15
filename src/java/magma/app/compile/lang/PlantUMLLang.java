package magma.app.compile.lang;

import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.LastRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.SuffixRule;

import java.util.List;

public class PlantUMLLang {
    public static Rule<NodeWithEverything> createPlantUMLRootRule() {
        return CommonLang.Divide(List.of(createDependencyRule(), new EmptyRule()));
    }

    private static Rule<NodeWithEverything> createDependencyRule() {
        final var parent = new StringRule("parent");
        final var child = new StringRule("child");
        return new SuffixRule(new LastRule(parent, " --> ", child), "\n");
    }
}
