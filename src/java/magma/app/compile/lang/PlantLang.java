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
import magma.app.compile.rule.TypeRule;

public class PlantLang {
    public static Rule<NodeWithEverything> createImplementsRule() {
        return new TypeRule<>("implements",
                LocateRule.First(ExtractRule.createStringRule("source", new MapNodeFactory()),
                        " --|> ",
                        ExtractRule.createStringRule("destination", new MapNodeFactory())));
    }

    public static Rule<NodeWithEverything> createPlantUMLClassesRule() {
        return new OrRule<>(Lists.of(createPlantUMLClassRule("class"), createPlantUMLClassRule("interface")));
    }

    public static Rule<NodeWithEverything> createPlantUMLClassRule(String type) {
        final var afterType = ExtractRule.createStringRule("name", new MapNodeFactory());
        return new TypeRule<>(type, ModifyRule.Prefix(type + " ", afterType));
    }

    public static Rule<NodeWithEverything> createPlantRootSegmentRule() {
        return ModifyRule.Suffix(new OrRule<>(Lists.of(JavaLang.createDependencyRule(),
                createPlantUMLClassesRule(),
                createImplementsRule())), "\n");
    }

    public static Rule<NodeWithEverything> createPlantRootRule() {
        return new NodeListRule<>("children", createPlantRootSegmentRule(), new MapNodeFactory());
    }
}