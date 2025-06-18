package magma.app.compile;

import magma.app.compile.rule.LocateRule;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.TypeRule;

import java.util.List;

public class Lang {
    public static Rule createDependencyRule() {
        return new SuffixRule(LocateRule.Last(new StringRule("source"), " --> ", new StringRule("destination")), "\n");
    }

    public static Rule createImportRule() {
        return new PrefixRule("import ",
                new SuffixRule(LocateRule.Last(new StringRule("temp"), ".", new StringRule("destination")), ";"));
    }

    public static Rule createPlantUMLRootSegmentRule() {
        return new SuffixRule(new OrRule(List.of(createPlantUMLClassesRule(), createImplementsRule())), "\n");
    }

    private static Rule createImplementsRule() {
        return new TypeRule("implements",
                LocateRule.First(new StringRule("source"), " --|> ", new StringRule("destination")));
    }

    private static Rule createPlantUMLClassesRule() {
        return new OrRule(List.of(createPlantUMLClassRule("class"), createPlantUMLClassRule("interface")));
    }

    public static Rule createStructureDefinitionsRule() {
        return new OrRule(List.of(createStructureDefinitionRule("class"),
                createStructureDefinitionRule("interface"),
                createStructureDefinitionRule("record")));
    }

    private static Rule createStructureDefinitionRule(String type) {
        final Rule beforeType = new StringRule("before-type");

        final Rule name = new StringRule("name");
        final Rule withParams = new OrRule(List.of(LocateRule.First(name, "(", new StringRule("params")), name));
        final Rule afterType = new OrRule(List.of(LocateRule.Last(withParams,
                " implements ",
                new StringRule("supertype")), withParams));

        return new TypeRule(type, LocateRule.First(beforeType, type + " ", afterType));
    }

    private static Rule createPlantUMLClassRule(String type) {
        final var afterType = new StringRule("name");
        return new TypeRule(type, new PrefixRule(type + " ", afterType));
    }
}
