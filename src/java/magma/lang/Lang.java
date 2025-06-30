package magma.lang;

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
import magma.string.result.StringResult;

import java.util.ArrayList;
import java.util.List;

public class Lang {
    public static final String LINE_SEPARATOR = System.lineSeparator();

    private Lang() {}

    public static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> createPlantRootRule() {
        return new DivideRule("children", Lang.createPlantRootSegmentRule());
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> createPlantRootSegmentRule() {
        final var options = new OrRule(List.of(Lang.createDependencyRule(), Lang.createPlantStructureRule()));
        return new SuffixRule(options, Lang.LINE_SEPARATOR);
    }

    public static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> createJavaRootSegmentRule() {
        final var header = (Rule<EverythingNode, NodeResult<EverythingNode>, StringResult>) new OrRule(
                List.of(Lang.createClassHeaderRule("class"), Lang.createClassHeaderRule("interface"),
                        Lang.createRecordHeaderRule()));


        final var rules = new ArrayList<>(List.of(Lang.createImportRule()));
        rules.add(Lang.getTypeRule(header));
        return new OrRule(rules);
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> createImportRule() {
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> child = new StringRule("child");
        return new TypeRule<>("import", new StripRule(
                new SuffixRule(new PrefixRule("import ", SplitRule.Last(new StringRule("discard"), ".", child)), ";")));
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> getTypeRule(final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> header) {
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> content = new StringRule("content");
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult>
                header1 = new OrRule(List.of(SplitRule.Last(header, " extends ", Lang.createTypeRule()), header));

        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> anImplements =
                new OrRule(List.of(SplitRule.Last(header1, "implements", Lang.createTypeRule()), header1));

        return new StripRule(new SuffixRule(SplitRule.First(anImplements, "{", content), "}"));
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> createTypeRule() {
        return new StripRule(
                new SuffixRule(SplitRule.First(new StringRule("base"), "<", new StringRule("value")), ">"));
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> createPlantStructureRule() {
        return new OrRule(
                List.of(Lang.createTypedPlantStructureRule("class"), Lang.createTypedPlantStructureRule("interface")));
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> createRecordHeaderRule() {
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> modifiers = new StringRule("modifiers");
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> name = new StripRule(new StringRule("name"));
        final Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> params = new StringRule("params");
        final var withParams = SplitRule.First(name, "(", params);
        final var afterKeyword = SplitRule.First(withParams, ")", new StringRule("more"));
        return new TypeRule<>("record", SplitRule.First(modifiers, "record ", afterKeyword));
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> createClassHeaderRule(final String type) {
        return new TypeRule<>(type, SplitRule.Last(new StringRule("discard"), type + " ", new StripRule(new StringRule("name"))));
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> createTypedPlantStructureRule(final String type) {
        return new TypeRule<>(type, new PrefixRule(type + " ", new StringRule("content")));
    }

    private static Rule<EverythingNode, NodeResult<EverythingNode>, StringResult> createDependencyRule() {
        return SplitRule.First(new StringRule("parent"), " <-- ", new StringRule("child"));
    }
}
