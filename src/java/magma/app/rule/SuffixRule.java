package magma.app.rule;

import magma.app.node.core.MergingNode;
import magma.app.rule.result.GenerationResult;
import magma.app.rule.result.MergingLexResult;
import magma.app.rule.result.optional.OptionalLexResult;

public record SuffixRule<N extends MergingNode<N>>(Rule<N, MergingLexResult<N>> rule,
                                                   String suffix) implements Rule<N, MergingLexResult<N>> {
    @Override
    public MergingLexResult<N> lex(String input) {
        if (!input.endsWith(this.suffix))
            return OptionalLexResult.createEmpty();

        final var slice = input.substring(0, input.length() - this.suffix.length());
        return this.rule.lex(slice);
    }

    @Override
    public GenerationResult generate(N node) {
        return this.rule.generate(node).map(value -> value + this.suffix);
    }
}