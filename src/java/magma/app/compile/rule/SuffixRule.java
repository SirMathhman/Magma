package magma.app.compile.rule;

import magma.app.compile.factory.ResultFactory;
import magma.app.compile.node.result.NodeResult;
import magma.app.compile.string.Appending;

public record SuffixRule<Node, Error, StringResult extends Appending<StringResult>, ErrorSequence>(
        Rule<Node, NodeResult<Node, Error>, StringResult> rule, String suffix,
        ResultFactory<Node, NodeResult<Node, Error>, StringResult, ErrorSequence> factory) implements Rule<Node, NodeResult<Node, Error>, StringResult> {
    @Override
    public NodeResult<Node, Error> lex(final String input) {
        if (input.endsWith(suffix))
            return rule.lex(input.substring(0, input.length() - suffix.length()));

        return factory.fromNodeError("Suffix '" + suffix + "' not present", input);
    }

    @Override
    public StringResult generate(final Node node) {
        return rule.generate(node)
                .appendSlice(suffix);
    }
}
