package magma.app;

import magma.OptionalLike;
import magma.app.node.Node;

public record LastRule(Rule leftRule, String infix, Rule rightRule) implements Rule {
    @Override
    public OptionalLike<Node> lex(final String input) {
        final var separator = input.lastIndexOf(this.infix);
        if (0 > separator)
            return OptionalLike.empty();

        final var infixLength = this.infix.length();
        final var destination = input.substring(separator + infixLength);
        return this.rightRule.lex(destination);
    }

    @Override
    public OptionalLike<String> generate(final Node node) {
        return this.leftRule.generate(node)
                .flatMap(leftResult -> this.rightRule.generate(node)
                        .map(rightResult -> leftResult + this.infix + rightResult));
    }
}