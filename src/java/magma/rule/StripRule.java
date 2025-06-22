package magma.rule;

import magma.node.EverythingNode;
import magma.node.result.NodeResult;
import magma.string.StringResult;

public record StripRule(Rule<EverythingNode, StringResult> rule) implements Rule<EverythingNode, StringResult> {
    @Override
    public NodeResult lex(final String input) {
        final var strip = input.strip();
        return this.rule.lex(strip);
    }

    @Override
    public StringResult generate(final EverythingNode node) {
        return this.rule.generate(node);
    }
}