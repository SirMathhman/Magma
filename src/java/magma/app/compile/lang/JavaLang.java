package magma.app.compile.lang;

import magma.api.collect.list.Lists;
import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.DivideRule;
import magma.app.compile.rule.LocateRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

public class JavaLang {
    public static Rule<NodeWithEverything> createDependencyRule() {
        return new TypeRule<>("dependency",
                new SuffixRule<>(LocateRule.Last(new StringRule<>("source", new MapNodeFactory()),
                        " --> ", new StringRule<>("destination", new MapNodeFactory())), "\n"));
    }

    public static Rule<NodeWithEverything> createImportRule() {
        return new TypeRule<>("import",
                new StripRule<>(new PrefixRule<>("import ",
                        new SuffixRule<>(LocateRule.Last(new StringRule<>("temp", new MapNodeFactory()),
                                ".",
                                new StringRule<>("destination", new MapNodeFactory())), ";"))));
    }

    public static Rule<NodeWithEverything> createStructureDefinitionsRule() {
        return new OrRule<>(Lists.of(createStructureDefinitionRule("class"),
                createStructureDefinitionRule("interface"),
                createStructureDefinitionRule("record")));
    }

    private static Rule<NodeWithEverything> createStructureDefinitionRule(String type) {
        final Rule<NodeWithEverything> beforeType = new StringRule<>("before-type", new MapNodeFactory());

        final Rule<NodeWithEverything> name = new StringRule<>("name", new MapNodeFactory());
        final Rule<NodeWithEverything> withTypeParams = new OrRule<>(Lists.of(new StripRule<>(new SuffixRule<>(
                LocateRule.First(name, "<", new StringRule<>("type-arguments", new MapNodeFactory())),
                ">")), name));

        final Rule<NodeWithEverything> withParams = new OrRule<>(Lists.of(LocateRule.First(withTypeParams,
                "(",
                new StringRule<>("params", new MapNodeFactory())), withTypeParams));
        final Rule<NodeWithEverything> afterType = new OrRule<>(Lists.of(LocateRule.Last(withParams,
                " implements ",
                new NodeRule<>("supertype", createTypeRule(), new MapNodeFactory())), withParams));

        return new TypeRule<>(type, LocateRule.First(beforeType, type + " ", afterType));
    }

    private static Rule<NodeWithEverything> createTypeRule() {
        return new OrRule<>(Lists.of(createGenericRule(), createIdentifierRule()));
    }

    private static Rule<NodeWithEverything> createIdentifierRule() {
        return new TypeRule<>("identifier", new StringRule<>("value", new MapNodeFactory()));
    }

    private static Rule<NodeWithEverything> createGenericRule() {
        return new TypeRule<>("generic",
                new StripRule<>(new SuffixRule<>(LocateRule.First(new StringRule<>("base", new MapNodeFactory()),
                        "<",
                        new StringRule<>("type-arguments", new MapNodeFactory())), ">")));
    }

    public static Rule<NodeWithEverything> createStructureRule() {
        return LocateRule.First(createStructureDefinitionsRule(),
                "{",
                new StringRule<>("with-braces", new MapNodeFactory()));
    }

    public static Rule<NodeWithEverything> createJavaRootSegmentRule() {
        return new OrRule<>(Lists.of(createImportRule(), createStructureRule()));
    }

    public static Rule<NodeWithEverything> createJavaRootRule() {
        return new DivideRule<>("children", createJavaRootSegmentRule(), new MapNodeFactory());
    }
}
