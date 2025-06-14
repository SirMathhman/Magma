package magma.app.compile.lang;

import magma.app.compile.Rule;
import magma.app.compile.node.CompoundNode;
import magma.app.compile.rule.InfixRule;
import magma.app.compile.rule.ModifyingRule;
import magma.app.compile.rule.NodeListRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.SuffixRule;
import magma.app.compile.rule.result.RuleResult;

public class Lang {
    public static Rule<CompoundNode, RuleResult<CompoundNode>, RuleResult<String>> createJavaRootRule() {
        return new NodeListRule("children", createImportRule());
    }

    public static Rule<CompoundNode, RuleResult<CompoundNode>, RuleResult<String>> createPlantRootRule() {
        return new NodeListRule("children", createDependencyRule());
    }

    static Rule<CompoundNode, RuleResult<CompoundNode>, RuleResult<String>> createImportRule() {
        final var parent = new StringRule("parent");
        final var destination = new StringRule("destination");
        final var rule = ModifyingRule.Prefix("import ", new SuffixRule<>(new InfixRule<>(parent, ".", destination), ";"));
        return new StripRule<>(rule);
    }

    static Rule<CompoundNode, RuleResult<CompoundNode>, RuleResult<String>> createDependencyRule() {
        return new SuffixRule<>(new InfixRule<>(new StringRule("source"), " --> ", new StringRule("destination")), "\n");
    }
}