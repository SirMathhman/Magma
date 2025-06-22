package magma.rule;

import magma.api.error.FormattedError;
import magma.node.result.NodeResult;
import magma.string.StringResult;

public record StripRule<Node, Error>(Rule<Node, NodeResult<Node, FormattedError>, StringResult<Error>> rule) implements
        Rule<Node, NodeResult<Node, FormattedError>, StringResult<Error>> {
    @Override
    public NodeResult<Node, FormattedError> lex(final String input) {
        return this.rule.lex(input.strip());
    }

    @Override
    public StringResult<Error> generate(final Node node) {
        return this.rule.generate(node);
    }
}