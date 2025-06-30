package magma.lang;

import magma.node.Node;
import magma.rule.DivideRule;
import magma.rule.OrRule;
import magma.rule.PrefixRule;
import magma.rule.Rule;
import magma.rule.SplitRule;
import magma.rule.StringRule;
import magma.rule.StripRule;
import magma.rule.SuffixRule;
import magma.rule.TypeRule;

import java.util.ArrayList;
import java.util.List;

public class Lang {
    public static final String LINE_SEPARATOR = System.lineSeparator();

    private Lang() {}

    public static Rule<Node> createPlantRootRule() {
        return new DivideRule("children", Lang.createPlantRootSegmentRule());
    }

    private static Rule<Node> createPlantRootSegmentRule() {
        final var options = new OrRule(List.of(Lang.createDependencyRule(), Lang.createPlantStructureRule()));
        return new SuffixRule(options, Lang.LINE_SEPARATOR);
    }

    public static Rule<Node> createJavaRootSegmentRule() {
        final var header = (Rule<Node>) new OrRule(
                List.of(Lang.createClassHeaderRule("class"), Lang.createClassHeaderRule("interface"),
                        Lang.createRecordHeaderRule()));


        final var rules = new ArrayList<>(List.of(Lang.createImportRule()));
        rules.add(Lang.getTypeRule(header));
        return new OrRule(rules);
    }

    private static Rule<Node> createImportRule() {
        final Rule<Node> child = new StringRule("child");
        return new TypeRule("import", new StripRule(
                new SuffixRule(new PrefixRule("import ", SplitRule.Last(new StringRule("discard"), ".", child)), ";")));
    }

    private static Rule<Node> getTypeRule(final Rule<Node> header) {
        final Rule<Node> content = new StringRule("content");
        return new StripRule(new SuffixRule(SplitRule.First(
                new OrRule(List.of(SplitRule.Last(header, "implements", Lang.createTypeRule()), header)), "{", content),
                                            "}"));
    }

    private static Rule<Node> createTypeRule() {
        return new StripRule(
                new SuffixRule(SplitRule.First(new StringRule("base"), "<", new StringRule("value")), ">"));
    }

    private static Rule<Node> createPlantStructureRule() {
        return new OrRule(
                List.of(Lang.createTypedPlantStructureRule("class"), Lang.createTypedPlantStructureRule("interface")));
    }

    private static Rule<Node> createRecordHeaderRule() {
        final Rule<Node> modifiers = new StringRule("modifiers");
        final Rule<Node> name = new StripRule(new StringRule("name"));
        final Rule<Node> params = new StringRule("params");
        final var withParams = SplitRule.First(name, "(", params);
        final var afterKeyword = SplitRule.First(withParams, ")", new StringRule("more"));
        return new TypeRule("record", SplitRule.First(modifiers, "record ", afterKeyword));
    }

    private static Rule<Node> createClassHeaderRule(final String type) {
        return new TypeRule(type, SplitRule.Last(new StringRule("discard"), type + " ",
                                                 new StripRule(new StringRule("name"))));
    }

    private static Rule<Node> createTypedPlantStructureRule(final String type) {
        return new TypeRule(type, new PrefixRule(type + " ", new StringRule("content")));
    }

    private static Rule<Node> createDependencyRule() {
        return SplitRule.First(new StringRule("parent"), " <-- ", new StringRule("child"));
    }
}
