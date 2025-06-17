package magma.app.compile;

import magma.app.compile.rule.InfixRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;
import magma.app.compile.rule.divide.DivideRule;
import magma.app.compile.rule.or.OrRule;

import java.util.List;

public class Lang {
    public static Rule createJavaRootRule() {
        return new DivideRule("children",
                new OrRule(List.of(createNamespacedRule("package"),
                        new TypeRule("import", createNamespacedRule("import")),
                        createStructureRule("class"),
                        createStructureRule("interface"),
                        createStructureRule("record"))));
    }

    static Rule createStructureRule(String type) {
        return new InfixRule(new StringRule("before-infix"), type + " ", new StringRule("after-infix"));
    }

    public static Rule createPlantRootRule() {
        return new DivideRule("children", new OrRule(List.of(createDependencyRule())));
    }

    static Rule createNamespacedRule(String type) {
        return new StripRule(new PrefixRule(type + " ", new SuffixRule(new StringRule("destination"), ";")));
    }

    static Rule createDependencyRule() {
        return new SuffixRule(new InfixRule(new StringRule("source"), " --> ", new StringRule("destination")), "\n");
    }
}
