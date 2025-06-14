package magma.app.rule;

import magma.app.rule.result.GenerationResult;
import magma.app.rule.result.LexResult;
import magma.app.rule.result.optional.OptionalLexResult;

public record PrefixRule<N>(String prefix, Rule<N> rule) implements Rule<N> {
    @Override
    public LexResult<N> lex(String input) {
        if (!input.startsWith(this.prefix))
            return OptionalLexResult.createEmpty();

        final var slice = input.substring(this.prefix.length());
        return this.rule.lex(slice);
    }

    @Override
    public GenerationResult generate(N node) {
        return this.rule.generate(node).map(value -> this.prefix + value);
    }
}