package magma.app.rule;

import magma.app.rule.result.GenerationResult;
import magma.app.rule.result.LexResult;

public interface Rule<N> {
    LexResult<N> lex(String input);

    GenerationResult generate(N node);
}
