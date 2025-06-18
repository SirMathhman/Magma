package magma.app.compile.lang;

import magma.app.compile.node.MapNodeFactory;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.DivideRule;
import magma.app.compile.rule.LocateRule;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

import java.util.List;

public class PlantLang {
    public static Rule<NodeWithEverything> createImplementsRule() {
        return new TypeRule<>("implements",
                LocateRule.First(new StringRule<>("source", new MapNodeFactory()),
                        " --|> ",
                        new StringRule<>("destination", new MapNodeFactory())));
    }

    public static Rule<NodeWithEverything> createPlantUMLClassesRule() {
        return new OrRule<>(List.of(createPlantUMLClassRule("class"), createPlantUMLClassRule("interface")));
    }

    public static Rule<NodeWithEverything> createPlantUMLClassRule(String type) {
        final var afterType = new StringRule<>("name", new MapNodeFactory());
        return new TypeRule<>(type, new PrefixRule<>(type + " ", afterType));
    }

    public static Rule<NodeWithEverything> createPlantRootSegmentRule() {
        return new SuffixRule<>(new OrRule<>(List.of(JavaLang.createDependencyRule(),
                createPlantUMLClassesRule(),
                createImplementsRule())), "\n");
    }

    public static Rule<NodeWithEverything> createPlantRootRule() {
        return new DivideRule<>("children", createPlantRootSegmentRule(), new MapNodeFactory());
    }
}