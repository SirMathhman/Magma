package magma.app.compile.lang;

import magma.api.collect.list.Lists;
import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.ExtractRule;
import magma.app.compile.rule.LocateRule;
import magma.app.compile.rule.ModifyRule;
import magma.app.compile.rule.NodeListRule;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.TypeRule;

public class JavaLang {
    public static Rule<NodeWithEverything> createDependencyRule() {
        return new TypeRule<>("dependency",
                ModifyRule.Suffix(LocateRule.Last(new StringRule<>("source", new MapNodeFactory()),
                        " --> ",
                        new StringRule<>("destination", new MapNodeFactory())), "\n"));
    }

    public static Rule<NodeWithEverything> createImportRule() {
        return new TypeRule<>("import",
                ModifyRule.Strip(ModifyRule.Prefix("import ",
                        ModifyRule.Suffix(LocateRule.Last(new StringRule<>("temp", new MapNodeFactory()),
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
        final Rule<NodeWithEverything> withTypeParams = new OrRule<>(Lists.of(ModifyRule.Strip(ModifyRule.Suffix(
                LocateRule.First(name, "<", new StringRule<>("type-arguments", new MapNodeFactory())),
                ">")), name));

        final Rule<NodeWithEverything> withParams = new OrRule<>(Lists.of(LocateRule.First(withTypeParams,
                "(",
                new StringRule<>("params", new MapNodeFactory())), withTypeParams));
        final Rule<NodeWithEverything> afterType = new OrRule<>(Lists.of(LocateRule.Last(withParams,
                " implements ",
                ExtractRule.Node("supertype", createTypeRule(), new MapNodeFactory())), withParams));

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
                ModifyRule.Strip(ModifyRule.Suffix(LocateRule.First(new StringRule<>("base", new MapNodeFactory()),
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
        return new NodeListRule<>("children", createJavaRootSegmentRule(), new MapNodeFactory());
    }
}
