package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.rule.result.RuleResult;
import magma.app.compile.rule.result.optional.ResultRuleResult;

public record PrefixRule<N>(String prefix,
                            Rule<N, RuleResult<N>, RuleResult<String>> rule) implements Rule<N, RuleResult<N>, RuleResult<String>> {
    @Override
    public RuleResult<N> lex(String input) {
        if (!input.startsWith(this.prefix))
            return ResultRuleResult.createFromString("Prefix '" + this.prefix + "' not present", input);

        final var slice = input.substring(this.prefix.length());
        return this.rule.lex(slice);
    }

    @Override
    public RuleResult<String> generate(N node) {
        return this.rule.generate(node).mapValue(value -> this.prefix + value);
    }
}