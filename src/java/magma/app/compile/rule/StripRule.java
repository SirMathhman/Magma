package magma.app.compile.rule;

import magma.app.compile.result.GenerateResult;
import magma.app.compile.result.LexResult;

public record StripRule<Node>(Rule<Node> rule) implements Rule<Node> {
    @Override
    public LexResult lex(final String input) {
        final var strip = input.strip();
        return this.rule.lex(strip);
    }

    @Override
    public GenerateResult generate(final Node node) {
        return this.rule.generate(node);
    }
}