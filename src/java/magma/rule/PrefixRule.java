package magma.rule;

import magma.node.Node;
import magma.node.result.NodeErr;
import magma.node.result.NodeResult;
import magma.string.StringResult;

public record PrefixRule(String prefix, Rule<Node, StringResult> rule) implements Rule<Node, StringResult> {
    @Override
    public NodeResult lex(final String input) {
        if (!input.startsWith(this.prefix()))
            return new NodeErr();

        final var slice = input.substring(this.prefix()
                .length());
        return this.rule()
                .lex(slice);
    }

    @Override
    public StringResult generate(final Node node) {
        return this.rule.generate(node)
                .prepend(this.prefix);
    }
}