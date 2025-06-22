package magma.rule;

import magma.error.CompileError;
import magma.error.StringContext;
import magma.node.result.NodeErr;
import magma.node.result.NodeResult;
import magma.string.StringResult;

public final class PrefixRule<Node> implements Rule<Node, NodeResult<Node>, StringResult> {
    private final String prefix;
    private final Rule<Node, NodeResult<Node>, StringResult> rule;

    public PrefixRule(final String prefix, final Rule<Node, NodeResult<Node>, StringResult> rule) {
        this.prefix = prefix;
        this.rule = rule;
    }

    @Override
    public NodeResult<Node> lex(final String input) {
        if (!input.startsWith(this.prefix))
            return new NodeErr<>(new CompileError("Prefix '" + this.prefix + "' not present",
                    new StringContext(input)));

        final var slice = input.substring(this.prefix.length());
        return this.rule.lex(slice);
    }

    @Override
    public StringResult generate(final Node node) {
        return this.rule.generate(node)
                .prepend(this.prefix);
    }
}