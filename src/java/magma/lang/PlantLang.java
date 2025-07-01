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
import magma.rule.StringRule;
import magma.rule.SuffixRule;
import magma.rule.TypeRule;
import magma.string.Strings;
import magma.string.result.StringResult;

import java.util.List;

public class PlantLang {
    private PlantLang() {}

    public static Rule<EverythingNode, NodeResult<EverythingNode, FormatError>, StringResult<FormatError>> createPlantRootRule() {
        return new DivideRule<>("children", PlantLang.createPlantRootSegmentRule(), ResultFactoryImpl.get());
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode, FormatError>, StringResult<FormatError>> createPlantRootSegmentRule() {
        final var options = new OrRule<>(List.of(PlantLang.createDependencyRule(), PlantLang.createPlantStructureRule()),
                                         ResultFactoryImpl.get());
        return new SuffixRule<>(options, Strings.LINE_SEPARATOR, ResultFactoryImpl.get());
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode, FormatError>, StringResult<FormatError>> createPlantStructureRule() {
        return new OrRule<>(List.of(PlantLang.createTypedPlantStructureRule("class"), PlantLang.createTypedPlantStructureRule("interface")),
                            ResultFactoryImpl.get());
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode, FormatError>, StringResult<FormatError>> createTypedPlantStructureRule(
            final String type) {
        return new TypeRule<>(type, new PrefixRule<>(type + " ",
                                                     new StringRule<>("content", ResultFactoryImpl.get(), new MapNodeFactory()),
                                                     ResultFactoryImpl.get()),
                              ResultFactoryImpl.get());
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode, FormatError>, StringResult<FormatError>> createDependencyRule() {
        return CommonLang.First(new StringRule<>("parent", ResultFactoryImpl.get(), new MapNodeFactory()), " <-- ",
                                new StringRule<>("child", ResultFactoryImpl.get(), new MapNodeFactory()));
    }
}
