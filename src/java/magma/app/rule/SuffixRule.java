package magma.app.rule;

import magma.app.factory.ResultFactory;
import magma.app.node.result.NodeResult;
import magma.app.string.Appending;

public record SuffixRule<Node, Error, StringResult extends Appending<StringResult>, ErrorSequence>(
        Rule<Node, NodeResult<Node, Error>, StringResult> rule, String suffix,
        ResultFactory<Node, NodeResult<Node, Error>, StringResult, ErrorSequence> factory) implements Rule<Node, NodeResult<Node, Error>, StringResult> {
    @Override
    public NodeResult<Node, Error> lex(final String input) {
        if (input.endsWith(this.suffix))
            return this.rule.lex(input.substring(0, input.length() - this.suffix.length()));

        return this.factory.fromNodeError("Suffix '" + this.suffix + "' not present", input);
    }

    @Override
    public StringResult generate(final Node node) {
        return this.rule.generate(node)
                .appendSlice(this.suffix);
    }
}
