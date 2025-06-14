package magma.app.rule;

import magma.app.rule.result.GenerationResult;
import magma.app.rule.result.LexResult;

public record StripRule<N, S extends LexResult<N, S>>(Rule<N, S> rule) implements Rule<N, S> {
    @Override
    public S lex(String input) {
        return this.rule.lex(input.strip());
    }

    @Override
    public GenerationResult generate(N node) {
        return this.rule.generate(node);
    }
}
