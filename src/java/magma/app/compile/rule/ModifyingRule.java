package magma.app.compile.rule;

import magma.api.result.Err;
import magma.app.compile.Rule;
import magma.app.compile.node.CompoundNode;
import magma.app.compile.rule.modify.Modifier;
import magma.app.compile.rule.modify.PrefixModifier;
import magma.app.compile.rule.result.RuleResult;
import magma.app.compile.rule.result.optional.ResultRuleResult;

public final class ModifyingRule<N> implements Rule<N, RuleResult<N>, RuleResult<String>> {
    private final Modifier modifier;
    private final Rule<N, RuleResult<N>, RuleResult<String>> rule;

    public ModifyingRule(Modifier modifier, Rule<N, RuleResult<N>, RuleResult<String>> rule) {
        this.modifier = modifier;
        this.rule = rule;
    }

    public static ModifyingRule<CompoundNode> Prefix(String anImport, SuffixRule<CompoundNode> rule) {
        return new ModifyingRule<>(new PrefixModifier(anImport), rule);
    }

    @Override
    public RuleResult<N> lex(String input) {
        return this.modifier.modify(input).map(this.rule::lex).orElseGet(() -> {
            return new ResultRuleResult<>(new Err<>(this.modifier.createError(input)));
        });
    }

    @Override
    public RuleResult<String> generate(N node) {
        return this.rule.generate(node).mapValue(this.modifier::generate);
    }
}