package magma.rule;

import magma.error.CompileError;
import magma.error.FormattedError;
import magma.error.StringContext;
import magma.node.result.NodeErr;
import magma.node.result.NodeResult;
import magma.string.StringResult;

public final class InfixRule<Node> implements Rule<Node, NodeResult<Node, FormattedError>, StringResult<FormattedError>> {
    private final Rule<Node, NodeResult<Node, FormattedError>, StringResult<FormattedError>> leftRule;
    private final String infix;
    private final Rule<Node, NodeResult<Node, FormattedError>, StringResult<FormattedError>> rightRule;

    public InfixRule(final Rule<Node, NodeResult<Node, FormattedError>, StringResult<FormattedError>> leftRule, final String infix, final Rule<Node, NodeResult<Node, FormattedError>, StringResult<FormattedError>> rightRule) {
        this.leftRule = leftRule;
        this.infix = infix;
        this.rightRule = rightRule;
    }

    @Override
    public NodeResult<Node, FormattedError> lex(final String input) {
        final var separator = input.lastIndexOf(this.infix);
        if (0 > separator)
            return new NodeErr<>(new CompileError("Infix '" + this.infix + "' not present", new StringContext(input)));

        final var rightSlice = input.substring(separator + this.infix.length());
        return this.rightRule.lex(rightSlice);
    }

    @Override
    public StringResult<FormattedError> generate(final Node node) {
        return this.leftRule.generate(node)
                .appendSlice(this.infix)
                .tryAppendResult(() -> this.rightRule.generate(node));
    }
}