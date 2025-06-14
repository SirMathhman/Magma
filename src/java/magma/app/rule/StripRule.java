package magma.app.rule;

import magma.app.rule.result.GenerationResult;
import magma.app.rule.result.MergingLexResult;

public record StripRule<N>(Rule<N> rule) implements Rule<N> {
    @Override
    public MergingLexResult<N> lex(String input) {
        return this.rule.lex(input.strip());
    }

    @Override
    public GenerationResult generate(N node) {
        return this.rule.generate(node);
    }
}
