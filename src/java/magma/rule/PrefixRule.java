package magma.rule;

import magma.node.Node;
import magma.option.None;
import magma.option.Option;

public record PrefixRule(String prefix, Rule rule) implements Rule {
    @Override
    public Option<Node> lex(final String input) {
        if (!input.startsWith(this.prefix()))
            return new None<>();

        final var slice = input.substring(this.prefix()
                .length());
        return this.rule()
                .lex(slice);
    }
}