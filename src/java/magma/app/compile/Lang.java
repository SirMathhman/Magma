package magma.app.compile;

import magma.app.compile.error.NodeResult;
import magma.app.compile.error.ResultFactoryImpl;
import magma.app.compile.error.StringResult;
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
    public static Rule<NodeResult, StringResult> createJavaRootRule() {
        return new DivideRule("children",
                new OrRule(List.of(createNamespacedRule("package"),
                        new TypeRule("import", createNamespacedRule("import"), ResultFactoryImpl.create()),
                        createStructureRule("class"),
                        createStructureRule("interface"),
                        createStructureRule("record")), ResultFactoryImpl.create()));
    }

    static Rule<NodeResult, StringResult> createStructureRule(String type) {
        return new InfixRule(new StringRule<>("before-infix", ResultFactoryImpl.create()),
                type + " ",
                new StringRule<>("after-infix", ResultFactoryImpl.create()),
                ResultFactoryImpl.create());
    }

    public static Rule<NodeResult, StringResult> createPlantRootRule() {
        return new DivideRule("children", new OrRule(List.of(createDependencyRule()), ResultFactoryImpl.create()));
    }

    static Rule<NodeResult, StringResult> createNamespacedRule(String type) {
        return new StripRule<>(new PrefixRule<>(type + " ",
                new SuffixRule<>(new StringRule<>("destination", ResultFactoryImpl.create()),
                        ";",
                        ResultFactoryImpl.create()),
                ResultFactoryImpl.create()));
    }

    static Rule<NodeResult, StringResult> createDependencyRule() {
        return new SuffixRule<>(new InfixRule(new StringRule<>("source", ResultFactoryImpl.create()),
                " --> ",
                new StringRule<>("destination", ResultFactoryImpl.create()),
                ResultFactoryImpl.create()), "\n", ResultFactoryImpl.create());
    }
}
