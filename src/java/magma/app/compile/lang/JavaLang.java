package magma.app.compile.lang;

import magma.api.collect.list.Lists;
import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.ExtractRule;
import magma.app.compile.rule.LocateRule;
import magma.app.compile.rule.ModifyRule;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.TypeRule;

public class JavaLang {
    public static Rule<NodeWithEverything> createImportRule() {
        return new TypeRule<>("import",
                ModifyRule.Strip(ModifyRule.Prefix("import ",
                        ModifyRule.Suffix(LocateRule.Last(ExtractRule.createStringRule("temp", new MapNodeFactory()),
                                ".",
                                ExtractRule.createStringRule("destination", new MapNodeFactory())), ";"))));
    }

    public static Rule<NodeWithEverything> createStructureDefinitionsRule() {
        return new OrRule<>(Lists.of(createStructureDefinitionRule("class"),
                createStructureDefinitionRule("interface"),
                createStructureDefinitionRule("record")));
    }

    private static Rule<NodeWithEverything> createStructureDefinitionRule(String type) {
        final Rule<NodeWithEverything> beforeType = ExtractRule.createStringRule("before-type", new MapNodeFactory());

        final Rule<NodeWithEverything> name = ExtractRule.createStringRule("name", new MapNodeFactory());
        final Rule<NodeWithEverything> withTypeParams = new OrRule<>(Lists.of(ModifyRule.Strip(ModifyRule.Suffix(
                LocateRule.First(name, "<", ExtractRule.createStringRule("type-arguments", new MapNodeFactory())),
                ">")), name));

        final Rule<NodeWithEverything> withParams = new OrRule<>(Lists.of(LocateRule.First(withTypeParams,
                "(",
                ExtractRule.createStringRule("params", new MapNodeFactory())), withTypeParams));
        final Rule<NodeWithEverything> afterType = new OrRule<>(Lists.of(LocateRule.Last(withParams,
                " implements ",
                ExtractRule.Node("supertype", createTypeRule(), new MapNodeFactory())), withParams));

        return new TypeRule<>(type, LocateRule.First(beforeType, type + " ", afterType));
    }

    private static Rule<NodeWithEverything> createTypeRule() {
        return new OrRule<>(Lists.of(createGenericRule(), createIdentifierRule()));
    }

    private static Rule<NodeWithEverything> createIdentifierRule() {
        return new TypeRule<>("identifier", ExtractRule.createStringRule("value", new MapNodeFactory()));
    }

    private static Rule<NodeWithEverything> createGenericRule() {
        return new TypeRule<>("generic",
                ModifyRule.Strip(ModifyRule.Suffix(LocateRule.First(ExtractRule.createStringRule("base",
                                new MapNodeFactory()),
                        "<",
                        ExtractRule.createStringRule("type-arguments", new MapNodeFactory())), ">")));
    }

    public static Rule<NodeWithEverything> createStructureRule() {
        return LocateRule.First(createStructureDefinitionsRule(),
                "{",
                ExtractRule.createStringRule("with-braces", new MapNodeFactory()));
    }

    public static Rule<NodeWithEverything> createJavaRootSegmentRule() {
        return new OrRule<>(Lists.of(createImportRule(), createStructureRule()));
    }

    public static Rule<NodeWithEverything> createJavaRootRule() {
        return ExtractRule.NodeList("children", createJavaRootSegmentRule(), new MapNodeFactory());
    }
}
