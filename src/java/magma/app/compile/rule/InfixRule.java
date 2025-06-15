package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.node.core.MergingNode;
import magma.app.compile.rule.result.RuleResult;
import magma.app.compile.rule.result.RuleResult.RuleResultErr;
import magma.app.compile.rule.result.RuleResult.RuleResultOk;

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
        return switch (nRuleResult1) {
            case RuleResultErr<N>(var error1) -> new RuleResultErr<>(error1);
            case RuleResultOk<N>(N value3) -> switch (this.rightRule.lex(rightString)) {
                case RuleResultErr<N>(var error) -> new RuleResultErr<>(error);
                case RuleResultOk<N>(N value2) -> new RuleResultOk<>(value3.merge(value2));
            };
        };
    }

    @Override
    public RuleResult<String> generate(N node) {
        final var leftResult = this.leftRule.generate(node);
        final var rightResult = this.rightRule.generate(node);

        return switch (leftResult) {
            case RuleResultErr<String>(var error1) -> new RuleResultErr<>(error1);
            case RuleResultOk<String>(String value2) -> switch (rightResult) {
                case RuleResultErr<String>(var error) -> new RuleResultErr<>(error);
                case RuleResultOk<String>(String value1) -> new RuleResultOk<>(value2 + this.infix + value1);
            };
        };
    }
}