package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.node.core.MergingNode;
import magma.app.compile.rule.result.LexResult;
import magma.app.compile.rule.result.optional.OptionalLexResult;

public record InfixRule<N extends MergingNode<N>>(Rule<N> leftRule, String infix,
                                                  Rule<N> rightRule) implements Rule<N> {
    @Override
    public LexResult<N> lex(String input) {
        final var index = input.lastIndexOf(this.infix);
        if (index < 0)
            return OptionalLexResult.createEmpty();

        final var leftString = input.substring(0, index);
        final var rightString = input.substring(index + this.infix.length());

        return this.leftRule.lex(leftString).flatMap(value -> this.rightRule.lex(rightString).map(value::merge));
    }

    @Override
    public LexResult<String> generate(N node) {
        final var leftResult = this.leftRule.generate(node);
        final var rightResult = this.rightRule.generate(node);

        return leftResult.flatMap(leftValue -> rightResult.map(rightValue -> leftValue + this.infix + rightValue));
    }
}