package magma.app.compile.rule;

import magma.app.compile.rule.result.GenerationResult;
import magma.app.compile.rule.result.LexResult;

public interface Rule<N> {
    LexResult<N> lex(String input);

    GenerationResult generate(N node);
}
