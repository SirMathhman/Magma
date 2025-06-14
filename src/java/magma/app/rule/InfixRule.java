package magma.app.rule;

import magma.app.node.Node;
import magma.app.rule.result.GenerationResult;

public record InfixRule(Rule leftRule, String infix, Rule rightRule) implements Rule {
    @Override
    public GenerationResult generate(Node node) {
        final var leftResult = this.leftRule.generate(node);
        final var rightResult = this.rightRule.generate(node);

        return leftResult.flatMap(leftValue -> rightResult.map(rightValue -> leftValue + this.infix + rightValue));
    }
}