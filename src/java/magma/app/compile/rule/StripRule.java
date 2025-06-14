package magma.app.compile.rule;

import magma.app.compile.Rule;

public record StripRule<N, L, G>(Rule<N, L, G> rule) implements Rule<N, L, G> {
    @Override
    public L lex(String input) {
        return this.rule.lex(input.strip());
    }

    @Override
    public G generate(N node) {
        return this.rule.generate(node);
    }
}
