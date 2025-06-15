package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.rule.modify.Modifier;
import magma.app.compile.rule.modify.PrefixModifier;
import magma.app.compile.rule.modify.SuffixModifier;
import magma.app.compile.rule.result.RuleResult;

import java.util.function.Function;

public final class ModifyingRule<N> implements Rule<N, RuleResult<N>, RuleResult<String>> {
    private final Modifier modifier;
    private final Rule<N, RuleResult<N>, RuleResult<String>> rule;

    public ModifyingRule(Modifier modifier, Rule<N, RuleResult<N>, RuleResult<String>> rule) {
        this.modifier = modifier;
        this.rule = rule;
    }

    public static <Node> ModifyingRule<Node> Prefix(String anImport, Rule<Node, RuleResult<Node>, RuleResult<String>> rule) {
        return new ModifyingRule<>(new PrefixModifier(anImport), rule);
    }

    public static <Node> Rule<Node, RuleResult<Node>, RuleResult<String>> createSuffixRule(Rule<Node, RuleResult<Node>, RuleResult<String>> rule, String suffix) {
        return new ModifyingRule<Node>(new SuffixModifier(suffix), rule);
    }

    @Override
    public RuleResult<N> lex(String input) {
        return this.modifier.modify(input).map(this.rule::lex).orElseGet(() -> {
            return new RuleResult.Err<>(this.modifier.createError(input));
        });
    }

    @Override
    public RuleResult<String> generate(N node) {
        RuleResult<String> stringRuleResult = this.rule.generate(node);
        return stringRuleResult.<RuleResult<String>>match(value -> new RuleResult.Ok<>(((Function<String, String>) this.modifier::generate).apply(value)), RuleResult.Err::new);
    }
}