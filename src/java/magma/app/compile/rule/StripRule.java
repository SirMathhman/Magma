package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.rule.result.RuleResult;

public record StripRule<N>(Rule<N> rule) implements Rule<N> {
    @Override
    public RuleResult<N> lex(String input) {
        return this.rule.lex(input.strip());
    }

    @Override
    public RuleResult<String> generate(N node) {
        return this.rule.generate(node);
    }
}
