package magma.app.compile;

import magma.api.result.Result;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.ResultFactoryImpl;
import magma.app.compile.node.Node;
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
    public static Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> createJavaRootRule() {
        return new DivideRule("children",
                new OrRule<>(List.of(createNamespacedRule("package"),
                        new TypeRule<>("import", createNamespacedRule("import"), ResultFactoryImpl.create()),
                        createStructureRule("class"),
                        createStructureRule("interface"),
                        createStructureRule("record")), ResultFactoryImpl.create()));
    }

    static Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> createStructureRule(String type) {
        return new InfixRule<>(new StringRule<FormattedError, Result<String, FormattedError>>("before-infix",
                ResultFactoryImpl.create()),
                type + " ",
                new StringRule<FormattedError, Result<String, FormattedError>>("after-infix",
                        ResultFactoryImpl.create()),
                ResultFactoryImpl.create());
    }

    public static Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> createPlantRootRule() {
        return new DivideRule("children", new OrRule<>(List.of(createDependencyRule()), ResultFactoryImpl.create()));
    }

    static Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> createNamespacedRule(String type) {
        return new StripRule<>(new PrefixRule<>(type + " ",
                new SuffixRule<>(new StringRule<FormattedError, Result<String, FormattedError>>("destination",
                        ResultFactoryImpl.create()),
                        ";",
                        ResultFactoryImpl.create()),
                ResultFactoryImpl.create()));
    }

    static Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> createDependencyRule() {
        return new SuffixRule<>(new InfixRule<>(new StringRule<FormattedError, Result<String, FormattedError>>("source",
                ResultFactoryImpl.create()),
                " --> ",
                new StringRule<FormattedError, Result<String, FormattedError>>("destination",
                        ResultFactoryImpl.create()),
                ResultFactoryImpl.create()), "\n", ResultFactoryImpl.create());
    }
}
