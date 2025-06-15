package magma.app.compile;

import magma.app.compile.rule.DivideRule;
import magma.app.compile.rule.LastRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;

public class Lang {
    public static DivideRule createJavaRootRule() {
        return new DivideRule("children", createImportRule());
    }

    public static DivideRule createPlantUMLRootRule() {
        return new DivideRule("children", createDependencyRule());
    }

    private static SuffixRule createDependencyRule() {
        final var parent = new StringRule("parent");
        final var child = new StringRule("child");
        return new SuffixRule(new LastRule(parent, " --> ", child), "\n");
    }

    private static StripRule createImportRule() {
        return new StripRule(new PrefixRule("import ", new SuffixRule(new LastRule(new StringRule("parent"), ".", new StringRule("child")), ";")));
    }
}
