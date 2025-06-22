package magma.app.compile.rule;

import magma.app.compile.result.NodeResult;
import magma.app.compile.result.StringResult;

public record StripRule<Node>(Rule<Node> rule) implements Rule<Node> {
    @Override
    public NodeResult lex(final String input) {
        final var strip = input.strip();
        return this.rule.lex(strip);
    }

    @Override
    public StringResult generate(final Node node) {
        return this.rule.generate(node);
    }
}