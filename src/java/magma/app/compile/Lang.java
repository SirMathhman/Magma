package magma.app.compile;

import magma.api.collect.iter.Iterable;
import magma.api.collect.list.Lists;

public class Lang {
    public static Rule<Node, NodeResult<Node, FormattedError, Iterable<FormattedError>>, StringResult<FormattedError, Iterable<FormattedError>>> createJavaRootRule() {
        return new DivideRule<>("children", new OrRule<>(Lists.of(createNamespacedRule("package"),
                        new TypeRule<>("import", createNamespacedRule("import"), ResultFactoryImpl.create()),
                        createStructureRule("class"),
                        createStructureRule("interface"), createStructureRule("record")), ResultFactoryImpl.create()),
                new MapNodeFactory(),
                ResultFactoryImpl.create());
    }

    static Rule<Node, NodeResult<Node, FormattedError, Iterable<FormattedError>>, StringResult<FormattedError, Iterable<FormattedError>>> createStructureRule(String type) {
        return new InfixRule<>(new StringRule<>("before-infix", ResultFactoryImpl.create(), new MapNodeFactory()),
                type + " ",
                new StringRule<>("after-infix", ResultFactoryImpl.create(), new MapNodeFactory()),
                ResultFactoryImpl.create());
    }

    public static Rule<Node, NodeResult<Node, FormattedError, Iterable<FormattedError>>, StringResult<FormattedError, Iterable<FormattedError>>> createPlantRootRule() {
        return new DivideRule<>("children", new OrRule<>(Lists.of(createDependencyRule()), ResultFactoryImpl.create()),
                new MapNodeFactory(),
                ResultFactoryImpl.create());
    }

    static Rule<Node, NodeResult<Node, FormattedError, Iterable<FormattedError>>, StringResult<FormattedError, Iterable<FormattedError>>> createNamespacedRule(String type) {
        return new StripRule<>(new PrefixRule<>(type + " ",
                new SuffixRule<>(new StringRule<>("destination", ResultFactoryImpl.create(), new MapNodeFactory()),
                        ";",
                        ResultFactoryImpl.create()),
                ResultFactoryImpl.create()));
    }

    static Rule<Node, NodeResult<Node, FormattedError, Iterable<FormattedError>>, StringResult<FormattedError, Iterable<FormattedError>>> createDependencyRule() {
        return new SuffixRule<>(new InfixRule<>(new StringRule<>("source",
                ResultFactoryImpl.create(),
                new MapNodeFactory()),
                " --> ",
                new StringRule<>("destination", ResultFactoryImpl.create(), new MapNodeFactory()),
                ResultFactoryImpl.create()), "\n", ResultFactoryImpl.create());
    }
}
