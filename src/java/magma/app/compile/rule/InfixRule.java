package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.node.core.MergingNode;
import magma.app.compile.rule.result.RuleResult;

import java.util.function.Function;

public record InfixRule<N extends MergingNode<N>>(Rule<N, RuleResult<N>, RuleResult<String>> leftRule, String infix,
                                                  Rule<N, RuleResult<N>, RuleResult<String>> rightRule) implements Rule<N, RuleResult<N>, RuleResult<String>> {
    @Override
    public RuleResult<N> lex(String input) {
        final var index = input.lastIndexOf(this.infix);
        if (index < 0)
            return RuleResult.createFromString("Infix '" + this.infix + "' not present", input);

        final var leftString = input.substring(0, index);
        final var rightString = input.substring(index + this.infix.length());

        RuleResult<N> nRuleResult1 = this.leftRule.lex(leftString);
        return nRuleResult1.match(value -> {
            RuleResult<N> nRuleResult = this.rightRule.lex(rightString);
            return nRuleResult.<RuleResult<N>>match(value1 -> new RuleResult.Ok<>(((Function<N, N>) value::merge).apply(value1)), RuleResult.Err::new);
        }, RuleResult.Err::new);
    }

    @Override
    public RuleResult<String> generate(N node) {
        final var leftResult = this.leftRule.generate(node);
        final var rightResult = this.rightRule.generate(node);

        return leftResult.match(leftValue -> rightResult.<RuleResult<String>>match(value -> new RuleResult.Ok<>(((Function<String, String>) rightValue -> leftValue + this.infix + rightValue).apply(value)), RuleResult.Err::new), RuleResult.Err::new);
    }
}