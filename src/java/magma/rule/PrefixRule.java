package magma.rule;

import magma.api.error.ErrorSequence;
import magma.factory.ResultFactory;
import magma.node.result.NodeResult;
import magma.string.StringResult;

public final class PrefixRule<Node, Error> implements Rule<Node, NodeResult<Node, Error>, StringResult<Error>> {
    private final String prefix;
    private final Rule<Node, NodeResult<Node, Error>, StringResult<Error>> rule;
    private final ResultFactory<Node, NodeResult<Node, Error>, StringResult<Error>, ErrorSequence<Error>> factory;

    public PrefixRule(final String prefix, final Rule<Node, NodeResult<Node, Error>, StringResult<Error>> rule, final ResultFactory<Node, NodeResult<Node, Error>, StringResult<Error>, ErrorSequence<Error>> factory) {
        this.prefix = prefix;
        this.rule = rule;
        this.factory = factory;
    }

    @Override
    public NodeResult<Node, Error> lex(final String input) {
        if (!input.startsWith(this.prefix))
            return this.factory.fromNodeError("Prefix '" + this.prefix + "' not present", input);

        final var slice = input.substring(this.prefix.length());
        return this.rule.lex(slice);
    }

    @Override
    public StringResult<Error> generate(final Node node) {
        return this.rule.generate(node)
                .prepend(this.prefix);
    }
}