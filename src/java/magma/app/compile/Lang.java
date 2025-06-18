package magma.app.compile;

import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.LocateRule;
import magma.app.compile.rule.NodeRule;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

import java.util.List;

public class Lang {
    public static Rule<NodeWithEverything> createDependencyRule() {
        return new SuffixRule<NodeWithEverything>(LocateRule.Last(new StringRule("source"),
                " --> ",
                new StringRule("destination")), "\n");
    }

    public static Rule<NodeWithEverything> createImportRule() {
        return new PrefixRule<NodeWithEverything>("import ",
                new SuffixRule<NodeWithEverything>(LocateRule.Last(new StringRule("temp"),
                        ".",
                        new StringRule("destination")), ";"));
    }

    public static Rule<NodeWithEverything> createPlantUMLRootSegmentRule() {
        return new SuffixRule<NodeWithEverything>(new OrRule<NodeWithEverything>(List.of(createPlantUMLClassesRule(),
                createImplementsRule())), "\n");
    }

    private static Rule<NodeWithEverything> createImplementsRule() {
        return new TypeRule<NodeWithEverything>("implements",
                LocateRule.First(new StringRule("source"), " --|> ", new StringRule("destination")));
    }

    private static Rule<NodeWithEverything> createPlantUMLClassesRule() {
        return new OrRule<NodeWithEverything>(List.of(createPlantUMLClassRule("class"),
                createPlantUMLClassRule("interface")));
    }

    public static Rule<NodeWithEverything> createStructureDefinitionsRule() {
        return new OrRule<NodeWithEverything>(List.of(createStructureDefinitionRule("class"),
                createStructureDefinitionRule("interface"),
                createStructureDefinitionRule("record")));
    }

    private static Rule<NodeWithEverything> createStructureDefinitionRule(String type) {
        final Rule<NodeWithEverything> beforeType = new StringRule("before-type");

        final Rule<NodeWithEverything> name = new StringRule("name");
        final Rule<NodeWithEverything> withTypeParams = new OrRule<NodeWithEverything>(List.of(new StripRule<NodeWithEverything>(
                new SuffixRule<NodeWithEverything>(LocateRule.First(
                name,
                "<",
                new StringRule("type-arguments")), ">")), name));

        final Rule<NodeWithEverything> withParams = new OrRule<NodeWithEverything>(List.of(LocateRule.First(
                        withTypeParams,
                        "(",
                        new StringRule("params")),
                withTypeParams));
        final Rule<NodeWithEverything> afterType = new OrRule<NodeWithEverything>(List.of(LocateRule.Last(withParams,
                " implements ", new NodeRule("supertype", createTypeRule())), withParams));

        return new TypeRule<NodeWithEverything>(type, LocateRule.First(beforeType, type + " ", afterType));
    }

    private static Rule<NodeWithEverything> createTypeRule() {
        return new OrRule<NodeWithEverything>(List.of(createGenericRule(), createIdentifierRule()));
    }

    private static Rule<NodeWithEverything> createIdentifierRule() {
        return new TypeRule<NodeWithEverything>("identifier", new StringRule("value"));
    }

    private static Rule<NodeWithEverything> createGenericRule() {
        return new TypeRule<NodeWithEverything>("generic",
                new StripRule<NodeWithEverything>(new SuffixRule<NodeWithEverything>(LocateRule.First(new StringRule(
                                "base"),
                        "<",
                        new StringRule("type-arguments")), ">")));
    }

    private static Rule<NodeWithEverything> createPlantUMLClassRule(String type) {
        final var afterType = new StringRule("name");
        return new TypeRule<NodeWithEverything>(type, new PrefixRule<NodeWithEverything>(type + " ", afterType));
    }
}
