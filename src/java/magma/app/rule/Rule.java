package magma.app.rule;

import magma.app.rule.result.GenerationResult;
import magma.app.rule.result.MergingLexResult;

public interface Rule<N> {
    MergingLexResult<N> lex(String input);

    GenerationResult generate(N node);
}
