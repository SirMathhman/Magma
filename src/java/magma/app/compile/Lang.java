package magma.app.compile;

import magma.app.compile.node.CompoundNode;
import magma.app.compile.rule.NodeListRule;
import magma.app.compile.rule.InfixRule;
import magma.app.compile.rule.PrefixRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;

public class Lang {
    public static Rule<CompoundNode> createJavaRootRule() {
        return new NodeListRule("children", createImportRule());
    }

    public static Rule<CompoundNode> createPlantRootRule() {
        return new NodeListRule("children", createDependencyRule());
    }

    static Rule<CompoundNode> createImportRule() {
        final var parent = new StringRule("parent");
        final var destination = new StringRule("destination");
        final var rule = new PrefixRule<>("import ", new SuffixRule<>(new InfixRule<>(parent, ".", destination), ";"));
        return new StripRule<>(rule);
    }

    static Rule<CompoundNode> createDependencyRule() {
        return new SuffixRule<>(new InfixRule<>(new StringRule("source"), " --> ", new StringRule("destination")), "\n");
    }
}