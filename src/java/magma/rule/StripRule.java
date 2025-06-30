package magma.rule;

import magma.node.Node;

import java.util.Optional;

public record StripRule(Rule<Node> rule) implements Rule<Node> {
    @Override
    public Optional<Node> lex(final String input) {
        final var strip = input.strip();
        return this.rule.lex(strip);
    }

    @Override
    public Optional<String> generate(final Node node) {
        return this.rule.generate(node);
    }
}