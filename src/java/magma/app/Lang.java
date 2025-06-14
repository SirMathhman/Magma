package magma.app;

import magma.app.node.CompoundNode;
import magma.app.rule.DivideRule;
import magma.app.rule.InfixRule;
import magma.app.rule.PrefixRule;
import magma.app.rule.Rule;
import magma.app.rule.StringRule;
import magma.app.rule.StripRule;
import magma.app.rule.SuffixRule;

public class Lang {
    public static Rule<CompoundNode> createJavaRootRule() {
        return new DivideRule("children", createImportRule());
    }

    public static Rule<CompoundNode> createPlantRootRule() {
        return new DivideRule("children", createDependencyRule());
    }

    static Rule<CompoundNode> createImportRule() {
        final var parent = new StringRule<CompoundNode>("parent");
        final var destination = new StringRule<CompoundNode>("destination");
        final var rule = new PrefixRule("import ", new SuffixRule(new InfixRule(parent, ".", destination), ";"));
        return new StripRule(rule);
    }

    static Rule<CompoundNode> createDependencyRule() {
        return new SuffixRule(new InfixRule(new StringRule<CompoundNode>("source"), " --> ", new StringRule<CompoundNode>("destination")), "\n");
    }
}