package magma.rule;

import magma.node.result.NodeErr;
import magma.node.result.NodeResult;

public record PrefixRule(String prefix, Rule rule) implements Rule {
    @Override
    public NodeResult lex(final String input) {
        if (!input.startsWith(this.prefix()))
            return new NodeErr();

        final var slice = input.substring(this.prefix()
                .length());
        return this.rule()
                .lex(slice);
    }
}