package magma.app.compile.rule;

import magma.app.compile.rule.result.GenerationResult;
import magma.app.compile.rule.result.LexResult;

public record StripRule<N>(Rule<N> rule) implements Rule<N> {
    @Override
    public LexResult<N> lex(String input) {
        return this.rule.lex(input.strip());
    }

    @Override
    public GenerationResult generate(N node) {
        return this.rule.generate(node);
    }
}
