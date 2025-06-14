package magma.app.rule;

import magma.app.node.Node;
import magma.app.rule.result.GenerationResult;
import magma.app.rule.result.LexResult;

import java.util.Optional;

public record InfixRule(Rule leftRule, String infix, Rule rightRule) implements Rule {
    @Override
    public LexResult lex(String input) {
        final var index = input.lastIndexOf(this.infix);
        if (index < 0) return LexResult.createEmpty();

        final var leftString = input.substring(0, index);
        final var rightString = input.substring(index + this.infix.length());

        return this.leftRule.lex(leftString).merge(() -> this.rightRule.lex(rightString));
    }

    @Override
    public GenerationResult generate(Node node) {
        final var leftResult = this.leftRule.generate(node);
        final var rightResult = this.rightRule.generate(node);

        return leftResult.flatMap(leftValue -> rightResult.map(rightValue -> leftValue + this.infix + rightValue));
    }

}