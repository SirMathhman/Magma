package magma.app;

import magma.app.node.Node;

import java.util.Optional;

public record StripRule(Rule rule) implements Rule {
    @Override
    public Optional<Node> lex(final String input) {
        final var strip = input.strip();
        return this.rule.lex(strip);
    }
}