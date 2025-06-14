package magma.app.compile;

import magma.app.compile.rule.result.LexResult;

public interface Rule<N> {
    LexResult<N> lex(String input);

    LexResult<String> generate(N node);
}