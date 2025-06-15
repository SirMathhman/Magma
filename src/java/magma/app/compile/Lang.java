package magma.app.compile;

import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.LastRule;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.divide.DivideRule;

import java.util.List;

public class Lang {
    public static Rule<NodeWithEverything> createJavaRootRule() {
        return Divide(List.of(createImportRule(), new StringRule("value")));
    }

    private static Rule<NodeWithEverything> Divide(List<Rule<NodeWithEverything>> rules) {
        return new DivideRule<>("children", new OrRule(rules), new MapNodeFactory());
    }

    public static Rule<NodeWithEverything> createPlantUMLRootRule() {
        return Divide(List.of(createDependencyRule(), new EmptyRule()));
    }

    private static Rule<NodeWithEverything> createDependencyRule() {
        final var parent = new StringRule("parent");
        final var child = new StringRule("child");
        return new SuffixRule(new LastRule(parent, " --> ", child), "\n");
    }

    private static Rule<NodeWithEverything> createImportRule() {
        return new StripRule(new PrefixRule("import ", new SuffixRule(new LastRule(new StringRule("parent"), ".", new StringRule("child")), ";")));
    }
}
