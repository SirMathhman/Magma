package magma.app.compile;

import magma.app.compile.merge.MergeRule;
import magma.app.compile.node.Node;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.divide.DivideRule;
import magma.app.compile.rule.or.OrRule;
import magma.app.compile.type.TypeRule;

import java.util.List;

public class Lang {
    public static Rule<Node> createJavaRootRule() {
        return new DivideRule("children",
                new OrRule<>(List.of(createNamespacedRule("package"),
                        new TypeRule<>("import", createNamespacedRule("import")),
                        createStructureRule("class"),
                        createStructureRule("interface"),
                        createStructureRule("record"))));
    }

    static Rule<Node> createStructureRule(String type) {
        return new MergeRule<>(new StringRule("before-infix"), type + " ", new StringRule("after-infix"));
    }

    public static Rule<Node> createPlantRootRule() {
        return new DivideRule("children", new OrRule<>(List.of(createDependencyRule())));
    }

    static Rule<Node> createNamespacedRule(String type) {
        return new StripRule<>(new PrefixRule<>(type + " ", new SuffixRule<>(new StringRule("destination"), ";")));
    }

    static Rule<Node> createDependencyRule() {
        return new SuffixRule<>(new MergeRule<>(new StringRule("source"), " --> ", new StringRule("destination")),
                "\n");
    }
}
