package magma.rule;

import magma.node.Node;

import java.util.Optional;

public record LastRule(Rule leftRule, String infix, Rule rightRule) implements Rule {
    @Override
    public Optional<Node> lex(final String input) {
        final var index = input.lastIndexOf(this.infix);
        if (0 > index) return Optional.empty();

        final var infixLength = this.infix.length();

        final var leftSlice = input.substring(0, index);
        final var rightSlice = input.substring(index + infixLength);
        return this.leftRule.lex(leftSlice)
                            .flatMap(leftResult -> this.rightRule.lex(rightSlice).map(leftResult::merge));
    }
}