package magma.rule;

import magma.node.EverythingNode;

import java.util.Optional;

public record StripRule(Rule<EverythingNode> rule) implements Rule<EverythingNode> {
    @Override
    public Optional<EverythingNode> lex(final String input) {
        final var strip = input.strip();
        return this.rule.lex(strip);
    }

    @Override
    public Optional<String> generate(final EverythingNode node) {
        return this.rule.generate(node);
    }
}