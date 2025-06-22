package magma.rule;

import magma.node.result.NodeResult;
import magma.string.StringResult;

public record StripRule<Node>(Rule<Node, StringResult> rule) implements Rule<Node, StringResult> {
    @Override
    public NodeResult<Node> lex(final String input) {
        return this.rule.lex(input.strip());
    }

    @Override
    public StringResult generate(final Node node) {
        return this.rule.generate(node);
    }
}