package magma.rule;

import magma.node.Node;

import java.util.Optional;

public record FirstRule(String infix, Rule rightRule) implements Rule {
    @Override
    public Optional<Node> lex(final String input) {
        final var index = input.lastIndexOf(this.infix);
        if (0 > index) return Optional.empty();

        final var infixLength = this.infix.length();
        final var rightSlice = input.substring(index + infixLength);
        return this.rightRule().lex(rightSlice);
    }
}