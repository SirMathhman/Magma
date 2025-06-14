package magma.app.rule;

import magma.app.MapNode;
import magma.app.rule.result.GenerationResult;

public record InfixRule(Rule leftRule, String infix, Rule rightRule) implements Rule {
    @Override
    public GenerationResult generate(MapNode mapNode) {
        final var leftResult = this.leftRule.generate(mapNode);
        final var rightResult = this.rightRule.generate(mapNode);

        return leftResult.flatMap(leftValue -> rightResult.map(rightValue -> leftValue + this.infix + rightValue));
    }
}