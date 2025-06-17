package magma.app.compile;

import magma.app.compile.error.ResultFactoryImpl;
import magma.app.compile.error.node.NodeResult;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.node.Node;
import magma.app.compile.rule.InfixRule;
import magma.app.compile.rule.MapNodeFactory;
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
    public static Rule<Node, NodeResult<Node>, StringResult> createJavaRootRule() {
        return new DivideRule("children",
                new OrRule<>(List.of(createNamespacedRule("package"),
                        new TypeRule<>("import", createNamespacedRule("import"), ResultFactoryImpl.create()),
                        createStructureRule("class"),
                        createStructureRule("interface"), createStructureRule("record")), ResultFactoryImpl.create()),
                new MapNodeFactory());
    }

    static Rule<Node, NodeResult<Node>, StringResult> createStructureRule(String type) {
        return new InfixRule<>(new StringRule<>("before-infix", ResultFactoryImpl.create(), new MapNodeFactory()),
                type + " ",
                new StringRule<>("after-infix", ResultFactoryImpl.create(), new MapNodeFactory()),
                ResultFactoryImpl.create());
    }

    public static Rule<Node, NodeResult<Node>, StringResult> createPlantRootRule() {
        return new DivideRule("children",
                new OrRule<>(List.of(createDependencyRule()), ResultFactoryImpl.create()),
                new MapNodeFactory());
    }

    static Rule<Node, NodeResult<Node>, StringResult> createNamespacedRule(String type) {
        return new StripRule<>(new PrefixRule<>(type + " ",
                new SuffixRule<>(new StringRule<>("destination", ResultFactoryImpl.create(), new MapNodeFactory()),
                        ";",
                        ResultFactoryImpl.create()),
                ResultFactoryImpl.create()));
    }

    static Rule<Node, NodeResult<Node>, StringResult> createDependencyRule() {
        return new SuffixRule<>(new InfixRule<>(new StringRule<>("source",
                ResultFactoryImpl.create(),
                new MapNodeFactory()),
                " --> ",
                new StringRule<>("destination", ResultFactoryImpl.create(), new MapNodeFactory()),
                ResultFactoryImpl.create()), "\n", ResultFactoryImpl.create());
    }
}
