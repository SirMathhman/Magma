package magma.rule;

import magma.compile.result.ResultFactory;
import magma.node.result.NodeResult;
import magma.string.result.StringResult;

public final class PrefixRule<Node, Error> implements Rule<Node, NodeResult<Node, Error, StringResult<Error>>, StringResult<Error>> {
    private final String prefix;
    private final Rule<Node, NodeResult<Node, Error, StringResult<Error>>, StringResult<Error>> rule;
    private final ResultFactory<Node, Error, StringResult<Error>> resultFactory;

    public PrefixRule(final String prefix,
                      final Rule<Node, NodeResult<Node, Error, StringResult<Error>>, StringResult<Error>> rule,
                      final ResultFactory<Node, Error, StringResult<Error>> resultFactory) {
        this.prefix = prefix;
        this.rule = rule;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult<Node, Error, StringResult<Error>> lex(final String input) {
        if (!input.startsWith(this.prefix))
            return this.resultFactory.createNodeError("Prefix '" + this.prefix + "' not present", input);
        final var prefixLength = this.prefix.length();

        final var slice = input.substring(prefixLength);
        return this.rule.lex(slice);
    }

    @Override
    public StringResult<Error> generate(final Node node) {
        return this.rule.generate(node).prependSlice(this.prefix);
    }
}