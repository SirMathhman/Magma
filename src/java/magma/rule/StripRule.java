package magma.rule;

import magma.node.Node;
import magma.option.Option;

public record StripRule(String name, Rule rule) implements Rule {
    @Override
    public Option<Node> lex(final String input) {
        final var strip = input.strip();
        return this.rule()
                .lex(strip);
    }
}