package magma.app.compile.rule;

import magma.app.compile.node.Node;
import magma.app.compile.result.GenerateResult;
import magma.app.compile.result.LexErr;
import magma.app.compile.result.LexResult;

public record LastRule(Rule leftRule, String infix, Rule rightRule) implements Rule {
    @Override
    public LexResult lex(final String input) {
        final var separator = input.lastIndexOf(this.infix);
        if (0 > separator)
            return new LexErr();

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