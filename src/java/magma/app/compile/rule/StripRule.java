package magma.app.compile.rule;

import magma.api.optional.OptionalLike;
import magma.app.compile.node.Node;

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