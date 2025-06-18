package magma.app.compile.lang;

import magma.api.collect.list.Lists;
import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.ExtractRules;
import magma.app.compile.rule.LocateRule;
import magma.app.compile.rule.ModifyRules;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.TypeRule;

public class PlantLang {
    public static Rule<NodeWithEverything> createImplementsRule() {
        return new TypeRule<>("implements",
                LocateRule.First(ExtractRules.createStringRule("source", new MapNodeFactory()),
                        " --|> ", ExtractRules.createStringRule("destination", new MapNodeFactory())));
    }

    public static Rule<NodeWithEverything> createPlantUMLClassesRule() {
        return new OrRule<>(Lists.of(createPlantUMLClassRule("class"), createPlantUMLClassRule("interface")));
    }

    public static Rule<NodeWithEverything> createPlantUMLClassRule(String type) {
        final var afterType = ExtractRules.createStringRule("name", new MapNodeFactory());
        return new TypeRule<>(type, ModifyRules.Prefix(type + " ", afterType));
    }

    public static Rule<NodeWithEverything> createPlantRootSegmentRule() {
        return ModifyRules.Suffix(new OrRule<>(Lists.of(createDependencyRule(),
                createPlantUMLClassesRule(),
                createImplementsRule())), "\n");
    }

    public static Rule<NodeWithEverything> createPlantRootRule() {
        return ExtractRules.NodeList("children", createPlantRootSegmentRule(), new MapNodeFactory());
    }

    public static Rule<NodeWithEverything> createDependencyRule() {
        return new TypeRule<>("dependency",
                ModifyRules.Suffix(LocateRule.Last(ExtractRules.createStringRule("source", new MapNodeFactory()),
                        " --> ", ExtractRules.createStringRule("destination", new MapNodeFactory())), "\n"));
    }
}