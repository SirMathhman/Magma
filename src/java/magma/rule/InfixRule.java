package magma.rule;

import magma.error.CompileError;
import magma.error.StringContext;
import magma.node.result.NodeErr;
import magma.node.result.NodeResult;
import magma.string.StringResult;

public final class InfixRule<Node> implements Rule<Node, NodeResult<Node>, StringResult> {
    private final Rule<Node, NodeResult<Node>, StringResult> leftRule;
    private final String infix;
    private final Rule<Node, NodeResult<Node>, StringResult> rightRule;

    public InfixRule(final Rule<Node, NodeResult<Node>, StringResult> leftRule, final String infix, final Rule<Node, NodeResult<Node>, StringResult> rightRule) {
        this.leftRule = leftRule;
        this.infix = infix;
        this.rightRule = rightRule;
    }

    @Override
    public NodeResult<Node> lex(final String input) {
        final var separator = input.lastIndexOf(this.infix);
        if (0 > separator)
            return new NodeErr<>(new CompileError("Infix '" + this.infix + "' not present", new StringContext(input)));

        final var rightSlice = input.substring(separator + this.infix.length());
        return this.rightRule.lex(rightSlice);
    }

    @Override
    public StringResult generate(final Node node) {
        return this.leftRule.generate(node)
                .appendSlice(this.infix)
                .tryAppendResult(() -> this.rightRule.generate(node));
    }
}