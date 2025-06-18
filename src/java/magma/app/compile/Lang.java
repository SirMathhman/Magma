package magma.app.compile;

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

import java.util.List;

public class Lang {
    public static Rule<NodeWithEverything> createDependencyRule() {
        return new TypeRule<>("dependency",
                new SuffixRule<>(LocateRule.Last(new StringRule("source"), " --> ", new StringRule("destination")),
                        "\n"));
    }

    public static Rule<NodeWithEverything> createImportRule() {
        return new TypeRule<>("import", new StripRule<>(new PrefixRule<>("import ",
                        new SuffixRule<>(LocateRule.Last(new StringRule("temp"), ".", new StringRule("destination")),
                                ";"))));
    }

    private static Rule<NodeWithEverything> createImplementsRule() {
        return new TypeRule<>("implements",
                LocateRule.First(new StringRule("source"), " --|> ", new StringRule("destination")));
    }

    private static Rule<NodeWithEverything> createPlantUMLClassesRule() {
        return new OrRule<>(List.of(createPlantUMLClassRule("class"), createPlantUMLClassRule("interface")));
    }

    public static Rule<NodeWithEverything> createStructureDefinitionsRule() {
        return new OrRule<>(List.of(createStructureDefinitionRule("class"),
                createStructureDefinitionRule("interface"),
                createStructureDefinitionRule("record")));
    }

    private static Rule<NodeWithEverything> createStructureDefinitionRule(String type) {
        final Rule<NodeWithEverything> beforeType = new StringRule("before-type");

        final Rule<NodeWithEverything> name = new StringRule("name");
        final Rule<NodeWithEverything> withTypeParams = new OrRule<>(List.of(new StripRule<>(new SuffixRule<>(LocateRule.First(
                name,
                "<",
                new StringRule("type-arguments")), ">")), name));

        final Rule<NodeWithEverything> withParams = new OrRule<>(List.of(LocateRule.First(withTypeParams,
                "(",
                new StringRule("params")), withTypeParams));
        final Rule<NodeWithEverything> afterType = new OrRule<>(List.of(LocateRule.Last(withParams,
                " implements ",
                new NodeRule("supertype", createTypeRule())), withParams));

        return new TypeRule<>(type, LocateRule.First(beforeType, type + " ", afterType));
    }

    private static Rule<NodeWithEverything> createTypeRule() {
        return new OrRule<>(List.of(createGenericRule(), createIdentifierRule()));
    }

    private static Rule<NodeWithEverything> createIdentifierRule() {
        return new TypeRule<>("identifier", new StringRule("value"));
    }

    private static Rule<NodeWithEverything> createGenericRule() {
        return new TypeRule<>("generic", new StripRule<>(new SuffixRule<>(LocateRule.First(new StringRule("base"),
                        "<",
                        new StringRule("type-arguments")), ">")));
    }

    private static Rule<NodeWithEverything> createPlantUMLClassRule(String type) {
        final var afterType = new StringRule("name");
        return new TypeRule<>(type, new PrefixRule<>(type + " ", afterType));
    }

    public static Rule<NodeWithEverything> createStructureRule() {
        return LocateRule.First(createStructureDefinitionsRule(), "{", new StringRule("with-braces"));
    }

    public static Rule<NodeWithEverything> createPlantRootSegmentRule() {
        return new SuffixRule<>(new OrRule<>(List.of(createDependencyRule(),
                createPlantUMLClassesRule(),
                createImplementsRule())), "\n");
    }

    public static Rule<NodeWithEverything> createJavaRootSegmentRule() {
        return new OrRule<>(List.of(createImportRule(), createStructureRule()));
    }

    public static DivideRule createJavaRootRule() {
        return new DivideRule("children", createJavaRootSegmentRule());
    }

    public static DivideRule createPlantRootRule() {
        return new DivideRule("children", createPlantRootSegmentRule());
    }
}
