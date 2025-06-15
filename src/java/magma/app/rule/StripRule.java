package magma.app.rule;

import magma.app.Rule;
import magma.app.maybe.MaybeNode;

public record StripRule(Rule rule) implements Rule {
    @Override
    public MaybeNode lex(String input) {
        final var strip = input.strip();
        return this.rule().lex(strip);
    }
}