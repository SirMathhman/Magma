package magma.app;

import magma.app.node.Node;
import magma.app.optional.OptionalLike;

public record StripRule(Rule rule) implements Rule {
    @Override
    public OptionalLike<Node> lex(final String input) {
        final var strip = input.strip();
        return this.rule.lex(strip);
    }

    @Override
    public OptionalLike<String> generate(final Node node) {
        return this.rule.generate(node);
    }
}