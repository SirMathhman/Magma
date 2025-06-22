package magma.app.compile.rule;

import magma.app.compile.node.result.NodeResult;
import magma.app.compile.string.StringResult;

public record StripRule<Node, Error>(
        Rule<Node, NodeResult<Node, Error>, StringResult<Error>> rule) implements Rule<Node, NodeResult<Node, Error>, StringResult<Error>> {
    @Override
    public NodeResult<Node, Error> lex(final String input) {
        return this.rule.lex(input.strip());
    }

    @Override
    public StringResult<Error> generate(final Node node) {
        return this.rule.generate(node);
    }
}