package magma.app.compile;

import magma.app.compile.rule.result.RuleResult;

public interface Rule<N> {
    RuleResult<N> lex(String input);

    RuleResult<String> generate(N node);
}