package magma.app.compile.rule;

import magma.api.error.list.ErrorSequence;
import magma.app.compile.factory.ResultFactory;
import magma.app.compile.node.result.NodeResult;
import magma.app.compile.string.StringResult;

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
        if (!input.startsWith(prefix))
            return factory.fromNodeError("Prefix '" + prefix + "' not present", input);

        final var slice = input.substring(prefix.length());
        return rule.lex(slice);
    }

    @Override
    public StringResult<Error> generate(final Node node) {
        return rule.generate(node)
                .prepend(prefix);
    }
}