package magma.rule;

import magma.error.CompileError;
import magma.error.FormattedError;
import magma.error.StringContext;
import magma.node.result.NodeErr;
import magma.node.result.NodeResult;
import magma.string.StringResult;

public final class PrefixRule<Node, Error> implements Rule<Node, NodeResult<Node, FormattedError>, StringResult<Error>> {
    private final String prefix;
    private final Rule<Node, NodeResult<Node, FormattedError>, StringResult<Error>> rule;

    public PrefixRule(final String prefix, final Rule<Node, NodeResult<Node, FormattedError>, StringResult<Error>> rule) {
        this.prefix = prefix;
        this.rule = rule;
    }

    @Override
    public NodeResult<Node, FormattedError> lex(final String input) {
        if (!input.startsWith(this.prefix))
            return new NodeErr<>(new CompileError("Prefix '" + this.prefix + "' not present",
                    new StringContext(input)));

        final var slice = input.substring(this.prefix.length());
        return this.rule.lex(slice);
    }

    @Override
    public StringResult<Error> generate(final Node node) {
        return this.rule.generate(node)
                .prepend(this.prefix);
    }
}