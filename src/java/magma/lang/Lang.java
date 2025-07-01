package magma.lang;

import magma.compile.result.ResultFactoryImpl;
import magma.error.FormatError;
import magma.node.EverythingNode;
import magma.node.result.NodeResult;
import magma.rule.DivideRule;
import magma.rule.OrRule;
import magma.rule.PrefixRule;
import magma.rule.Rule;
import magma.rule.SplitRule;
import magma.rule.StringRule;
import magma.rule.StripRule;
import magma.rule.SuffixRule;
import magma.rule.TypeRule;
import magma.string.Strings;
import magma.string.result.StringResult;

import java.util.ArrayList;
import java.util.List;

public class Lang {
    private Lang() {}

    public static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createPlantRootRule() {
        return new DivideRule("children", Lang.createPlantRootSegmentRule());
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createPlantRootSegmentRule() {
        final var options = new OrRule(List.of(Lang.createDependencyRule(), Lang.createPlantStructureRule()));
        return new SuffixRule(options, Strings.LINE_SEPARATOR);
    }

    public static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createJavaRootSegmentRule() {
        final var rules = new ArrayList<>(List.of(Lang.createImportRule(), Lang.getTypeRule(),
                                                  new TypeRule<>("placeholder", new StringRule("value"),
                                                                 ResultFactoryImpl.createResultFactory())));
        return new OrRule(rules);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> getTypeRule() {
        return Lang.getTypeRule(new OrRule(
                List.of(Lang.createClassHeaderRule("class"), Lang.createClassHeaderRule("interface"),
                        Lang.createRecordHeaderRule())));
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createImportRule() {
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> child = new StringRule("child");
        return new TypeRule<>("import", new StripRule(
                new SuffixRule(new PrefixRule("import ", SplitRule.Last(new StringRule("discard"), ".", child)), ";")),
                              ResultFactoryImpl.createResultFactory());
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> getTypeRule(final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> header) {
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> content = new StringRule("content");
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> header1 =
                new OrRule(List.of(SplitRule.Last(header, " extends ", Lang.createTypeRule()), header));

        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> anImplements =
                new OrRule(List.of(SplitRule.Last(header1, "implements", Lang.createTypeRule()), header1));

        return new StripRule(new SuffixRule(SplitRule.First(anImplements, "{", content), "}"));
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createTypeRule() {
        return new StripRule(
                new SuffixRule(SplitRule.First(new StringRule("base"), "<", new StringRule("value")), ">"));
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createPlantStructureRule() {
        return new OrRule(
                List.of(Lang.createTypedPlantStructureRule("class"), Lang.createTypedPlantStructureRule("interface")));
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createRecordHeaderRule() {
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> modifiers = new StringRule("modifiers");
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> name =
                new StripRule(new StringRule("name"));
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> params = new StringRule("params");
        final var withParams = SplitRule.First(name, "(", params);
        final var afterKeyword = SplitRule.First(withParams, ")", new StringRule("more"));
        return new TypeRule<>("record", SplitRule.First(modifiers, "record ", afterKeyword),
                              ResultFactoryImpl.createResultFactory());
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createClassHeaderRule(final String type) {
        return new TypeRule<>(type, SplitRule.Last(new StringRule("discard"), type + " ",
                                                   new StripRule(new StringRule("name"))),
                              ResultFactoryImpl.createResultFactory());
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createTypedPlantStructureRule(final String type) {
        return new TypeRule<>(type, new PrefixRule(type + " ", new StringRule("content")),
                              ResultFactoryImpl.createResultFactory());
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult<FormatError>> createDependencyRule() {
        return SplitRule.First(new StringRule("parent"), " <-- ", new StringRule("child"));
    }
}
