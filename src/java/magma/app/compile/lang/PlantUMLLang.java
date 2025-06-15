package magma.app.compile.lang;

import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.LastRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.SuffixRule;

import java.util.List;

public class PlantUMLLang {
    public static Rule<NodeWithEverything> createPlantUMLRootRule() {
        return CommonLang.Divide(List.of(createDependencyRule(), new EmptyRule<>(new MapNodeFactory())));
    }

    private static Rule<NodeWithEverything> createDependencyRule() {
        final var parent = new StringRule<>("parent", new MapNodeFactory());
        final var child = new StringRule<>("child", new MapNodeFactory());
        return new SuffixRule<>(new LastRule<>(parent, " --> ", child), "\n");
    }
}
