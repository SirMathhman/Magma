package magma.rule;

import magma.node.Node;
import magma.node.result.NodeResult;
import magma.string.StringResult;

public record StripRule(String name, Rule<Node, StringResult> rule) implements Rule<Node, StringResult> {
    @Override
    public NodeResult lex(final String input) {
        final var strip = input.strip();
        return this.rule()
                .lex(strip);
    }

    @Override
    public StringResult generate(final Node node) {
        return this.rule.generate(node);
    }
}