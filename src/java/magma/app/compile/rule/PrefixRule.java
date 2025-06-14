package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.rule.result.RuleResult;
import magma.app.compile.rule.result.optional.OptionalLexResult;

public record PrefixRule<N>(String prefix, Rule<N> rule) implements Rule<N> {
    @Override
    public RuleResult<N> lex(String input) {
        if (!input.startsWith(this.prefix))
            return OptionalLexResult.createEmpty();

        final var slice = input.substring(this.prefix.length());
        return this.rule.lex(slice);
    }

    @Override
    public RuleResult<String> generate(N node) {
        return this.rule.generate(node).map(value -> this.prefix + value);
    }
}