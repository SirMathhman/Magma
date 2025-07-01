package magma.rule;

import magma.compile.result.ResultFactory;
import magma.error.FormatError;
import magma.node.result.NodeResult;
import magma.string.result.StringResult;

public final class PrefixRule<Node> implements Rule<Node, NodeResult<Node, FormatError>, StringResult<FormatError>> {
    private final String prefix;
    private final Rule<Node, NodeResult<Node, FormatError>, StringResult<FormatError>> rule;
    private final ResultFactory<Node, FormatError, StringResult<FormatError>> resultFactory;

    public PrefixRule(final String prefix,
                      final Rule<Node, NodeResult<Node, FormatError>, StringResult<FormatError>> rule,
                      final ResultFactory<Node, FormatError, StringResult<FormatError>> resultFactory) {
        this.prefix = prefix;
        this.rule = rule;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult<Node, FormatError> lex(final String input) {
        if (!input.startsWith(this.prefix))
            return this.resultFactory.createNodeError("Prefix '" + this.prefix + "' not present", input);
        final var prefixLength = this.prefix.length();

        final var slice = input.substring(prefixLength);
        return this.rule.lex(slice);
    }

    @Override
    public StringResult<FormatError> generate(final Node node) {
        return this.rule.generate(node).prependSlice(this.prefix);
    }
}