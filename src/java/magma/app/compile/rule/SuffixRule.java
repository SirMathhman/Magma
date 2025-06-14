package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.rule.result.RuleResult;
import magma.app.compile.rule.result.optional.OptionalLexResult;

public record SuffixRule<N>(Rule<N, RuleResult<N>, RuleResult<String>> rule,
                            String suffix) implements Rule<N, RuleResult<N>, RuleResult<String>> {
    @Override
    public RuleResult<N> lex(String input) {
        if (!input.endsWith(this.suffix))
            return OptionalLexResult.createEmpty();

        final var slice = input.substring(0, input.length() - this.suffix.length());
        return this.rule.lex(slice);
    }

    @Override
    public RuleResult<String> generate(N node) {
        return this.rule.generate(node).map(value -> value + this.suffix);
    }
}