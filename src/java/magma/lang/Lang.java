package magma.lang;

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

    public static Rule createPlantRootRule() {
        return new DivideRule("children", Lang.createPlantRootSegmentRule());
    }

    private static Rule createPlantRootSegmentRule() {
        final var options = new OrRule(List.of(Lang.createDependencyRule(), Lang.createPlantStructureRule()));
        return new SuffixRule(options, Lang.LINE_SEPARATOR);
    }

    public static Rule createJavaRootSegmentRule() {
        final var header = (Rule) new OrRule(
                List.of(Lang.createClassHeaderRule("class"), Lang.createClassHeaderRule("interface"),
                        Lang.createRecordHeaderRule()));


        final var rules = new ArrayList<>(List.of(Lang.createImportRule()));
        rules.add(Lang.getTypeRule(header));

        return new OrRule(rules);
    }

    private static Rule createImportRule() {
        final Rule child = new StringRule("child");
        return new TypeRule("import", new StripRule(
                new SuffixRule(new PrefixRule("import ", SplitRule.Last(new StringRule("discard"), ".", child)), ";")));
    }

    private static Rule getTypeRule(final Rule header) {
        final Rule content = new StringRule("content");
        return new StripRule(new SuffixRule(SplitRule.First(header, "{", content), "}"));
    }

    private static Rule createPlantStructureRule() {
        return new OrRule(
                List.of(Lang.createTypedPlantStructureRule("class"), Lang.createTypedPlantStructureRule("interface")));
    }

    private static Rule createRecordHeaderRule() {
        final Rule modifiers = new StringRule("modifiers");
        final Rule name = new StringRule("name");
        final Rule params = new StringRule("params");
        final var withParams = SplitRule.First(name, "(", params);
        final var afterKeyword = SplitRule.First(withParams, ")", new StringRule("more"));
        return new TypeRule("record", SplitRule.First(modifiers, "record ", afterKeyword));
    }

    private static Rule createClassHeaderRule(final String type) {
        return new TypeRule(type,
                            SplitRule.Last(new StringRule("discard"), type + " ", new StringRule("before-content")));
    }

    private static Rule createTypedPlantStructureRule(final String type) {
        return new TypeRule(type, new PrefixRule(type + " ", new StringRule("before-content")));
    }

    private static Rule createDependencyRule() {
        return SplitRule.First(new StringRule("parent"), " <-- ", new StringRule("child"));
    }
}
