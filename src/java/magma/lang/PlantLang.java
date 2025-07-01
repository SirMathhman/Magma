package magma.lang;

import magma.compile.result.ResultFactoryImpl;
import magma.error.FormatError;
import magma.node.EverythingNode;
import magma.node.factory.MapNodeFactory;
import magma.node.result.NodeResult;
import magma.rule.DivideRule;
import magma.rule.OrRule;
import magma.rule.PrefixRule;
import magma.rule.Rule;
import magma.rule.SplitRule;
import magma.rule.StringRule;
import magma.rule.SuffixRule;
import magma.rule.TypeRule;
import magma.string.Strings;
import magma.string.result.StringResult;

import java.util.List;

public class PlantLang {
    private PlantLang() {}

    public static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createPlantRootRule() {
        return new DivideRule<EverythingNode>("children", PlantLang.createPlantRootSegmentRule(), ResultFactoryImpl.get());
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createPlantRootSegmentRule() {
        final var options = new OrRule<EverythingNode>(List.of(PlantLang.createDependencyRule(), PlantLang.createPlantStructureRule()),
                                                       ResultFactoryImpl.get());
        return new SuffixRule<EverythingNode>(options, Strings.LINE_SEPARATOR, ResultFactoryImpl.get());
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createPlantStructureRule() {
        return new OrRule<EverythingNode>(List.of(PlantLang.createTypedPlantStructureRule("class"), PlantLang.createTypedPlantStructureRule("interface")),
                                          ResultFactoryImpl.get());
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createTypedPlantStructureRule(
            final String type) {
        return new TypeRule<>(type, new PrefixRule(type + " ", new StringRule<EverythingNode>("content", ResultFactoryImpl.get(),
                                                                                              new MapNodeFactory())),
                              ResultFactoryImpl.get());
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createDependencyRule() {
        return SplitRule.First(new StringRule<EverythingNode>("parent", ResultFactoryImpl.get(), new MapNodeFactory()), " <-- ", new StringRule<EverythingNode>("child",
                                                                                                                                                                ResultFactoryImpl.get(),
                                                                                                                                                                new MapNodeFactory()));
    }
}
