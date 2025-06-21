package magma.app.compile.rule;

import magma.app.compile.node.Node;
import magma.app.compile.result.CompileError;
import magma.app.compile.result.GenerateResult;
import magma.app.compile.result.LexErr;
import magma.app.compile.result.LexResult;

public record LastRule(Rule<Node> leftRule, String infix, Rule<Node> rightRule) implements Rule<Node> {
    @Override
    public LexResult lex(final String input) {
        final var separator = input.lastIndexOf(this.infix);
        if (0 > separator)
            return new LexErr(new CompileError("Infix '" + this.infix + "' not present", input));

        final var infixLength = this.infix.length();
        final var destination = input.substring(separator + infixLength);
        return this.rightRule.lex(destination);
    }

    @Override
    public GenerateResult generate(final Node node) {
        return this.leftRule.generate(node)
                .appendSlice(this.infix)
                .appendResult(() -> this.rightRule.generate(node));
    }
}