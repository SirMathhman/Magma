package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.node.core.MergingNode;
import magma.app.compile.rule.result.RuleResult;

public record InfixRule<N extends MergingNode<N>>(Rule<N, RuleResult<N>, RuleResult<String>> leftRule, String infix,
                                                  Rule<N, RuleResult<N>, RuleResult<String>> rightRule) implements Rule<N, RuleResult<N>, RuleResult<String>> {
    @Override
    public RuleResult<N> lex(String input) {
        final var index = input.lastIndexOf(this.infix);
        if (index < 0)
            return RuleResult.createFromString("Infix '" + this.infix + "' not present", input);

        final var leftString = input.substring(0, index);
        final var rightString = input.substring(index + this.infix.length());

        return this.leftRule.lex(leftString).flatMap(value -> this.rightRule.lex(rightString).mapValue(value::merge));
    }

    @Override
    public RuleResult<String> generate(N node) {
        final var leftResult = this.leftRule.generate(node);
        final var rightResult = this.rightRule.generate(node);

        return leftResult.flatMap(leftValue -> rightResult.mapValue(rightValue -> leftValue + this.infix + rightValue));
    }
}