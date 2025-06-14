package magma.app;

import magma.app.rule.DivideRule;
import magma.app.rule.InfixRule;
import magma.app.rule.PrefixRule;
import magma.app.rule.Rule;
import magma.app.rule.StringRule;
import magma.app.rule.StripRule;
import magma.app.rule.SuffixRule;

public class Lang {
    public static Rule createJavaRootRule() {
        return new DivideRule("children", createImportRule());
    }

    public static Rule createPlantRootRule() {
        return new DivideRule("children", createDependencyRule());
    }

    static Rule createImportRule() {
        final var parent = new StringRule("parent");
        final var destination = new StringRule("destination");
        final var rule = new PrefixRule("import ", new SuffixRule(new InfixRule(parent, ".", destination), ";"));
        return new StripRule(rule);
    }

    static Rule createDependencyRule() {
        return new SuffixRule(new InfixRule(new StringRule("source"), " --> ", new StringRule("destination")), "\n");
    }
}