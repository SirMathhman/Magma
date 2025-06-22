package magma.rule;

import magma.error.CompileError;
import magma.error.StringContext;
import magma.node.EverythingNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeResult;
import magma.string.StringResult;

public record InfixRule(Rule<EverythingNode, StringResult> leftRule, String infix,
                        Rule<EverythingNode, StringResult> rightRule) implements Rule<EverythingNode, StringResult> {
    @Override
    public NodeResult lex(final String input) {
        final var separator = input.lastIndexOf(this.infix);
        if (0 > separator)
            return new NodeErr(new CompileError("Infix '" + this.infix + "' not present", new StringContext(input)));

        final var rightSlice = input.substring(separator + this.infix.length());
        return this.rightRule.lex(rightSlice);
    }

    @Override
    public StringResult generate(final EverythingNode node) {
        return this.leftRule.generate(node)
                .appendSlice(this.infix)
                .tryAppendResult(() -> this.rightRule.generate(node));
    }
}