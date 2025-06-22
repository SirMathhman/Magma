package magma.rule;

import magma.node.result.NodeResult;

public record StripRule(String name, Rule rule) implements Rule {
    @Override
    public NodeResult lex(final String input) {
        final var strip = input.strip();
        return this.rule()
                .lex(strip);
    }
}