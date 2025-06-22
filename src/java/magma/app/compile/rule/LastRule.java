package magma.app.compile.rule;

import magma.app.compile.node.Node;
import magma.app.compile.result.CompileError;
import magma.app.compile.result.NodeErr;
import magma.app.compile.result.NodeResult;
import magma.app.compile.result.StringResult;

public record LastRule(Rule<Node> leftRule, String infix, Rule<Node> rightRule) implements Rule<Node> {
    @Override
    public NodeResult lex(final String input) {
        final var separator = input.lastIndexOf(this.infix);
        if (0 > separator)
            return new NodeErr(new CompileError("Infix '" + this.infix + "' not present", input));

        final var infixLength = this.infix.length();
        final var destination = input.substring(separator + infixLength);
        return this.rightRule.lex(destination);
    }

    @Override
    public StringResult generate(final Node node) {
        return this.leftRule.generate(node)
                .appendSlice(this.infix)
                .appendResult(() -> this.rightRule.generate(node));
    }
}