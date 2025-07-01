package magma.rule;

import magma.error.FormatError;
import magma.node.result.NodeResult;
import magma.string.result.StringResult;

public record StripRule<Node>(Rule<Node, NodeResult<Node, FormatError>, StringResult<FormatError>> rule)
        implements Rule<Node, NodeResult<Node, FormatError>, StringResult<FormatError>> {
    @Override
    public NodeResult<Node, FormatError> lex(final String input) {
        return this.rule.lex(input.strip());
    }

    @Override
    public StringResult<FormatError> generate(final Node node) {
        return this.rule.generate(node);
    }
}