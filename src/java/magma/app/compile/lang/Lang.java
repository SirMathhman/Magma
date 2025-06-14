package magma.app.compile.lang;

import magma.app.compile.Rule;
import magma.app.compile.node.CompoundNode;
import magma.app.compile.rule.EmptyRule;
import magma.app.compile.rule.InfixRule;
import magma.app.compile.rule.ModifyingRule;
import magma.app.compile.rule.NodeListRule;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.StringRule;
import magma.app.compile.rule.StripRule;
import magma.app.compile.rule.result.RuleResult;

import java.util.List;

public class Lang {
    public static Rule<CompoundNode, RuleResult<CompoundNode>, RuleResult<String>> createJavaRootRule() {
        return new NodeListRule("children", new OrRule(List.of(
                createImportRule(),
                new StringRule("value")
        )));
    }

    public static Rule<CompoundNode, RuleResult<CompoundNode>, RuleResult<String>> createPlantRootRule() {
        return new NodeListRule("children", new OrRule<>(List.of(
                createDependencyRule(),
                new EmptyRule()
        )));
    }

    static Rule<CompoundNode, RuleResult<CompoundNode>, RuleResult<String>> createImportRule() {
        final var parent = new StringRule("parent");
        final var destination = new StringRule("destination");
        final var rule = ModifyingRule.Prefix("import ", ModifyingRule.createSuffixRule(new InfixRule<>(parent, ".", destination), ";"));
        return new StripRule<>(rule);
    }

    static Rule<CompoundNode, RuleResult<CompoundNode>, RuleResult<String>> createDependencyRule() {
        return ModifyingRule.createSuffixRule(new InfixRule<>(new StringRule("source"), " --> ", new StringRule("destination")), "\n");
    }
}