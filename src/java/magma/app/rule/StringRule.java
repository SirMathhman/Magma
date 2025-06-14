package magma.app.rule;

import magma.app.node.core.StringNode;
import magma.app.rule.factory.NodeFactory;
import magma.app.rule.result.GenerationResult;
import magma.app.rule.result.LexResult;
import magma.app.rule.result.optional.OptionalGenerationResult;
import magma.app.rule.result.optional.OptionalLexResult;

public final class StringRule<N extends StringNode<N>> implements Rule<N> {
    private final String key;
    private final NodeFactory<N> factory;

    public StringRule(String key, NodeFactory<N> factory) {
        this.key = key;
        this.factory = factory;
    }

    @Override
    public LexResult<N> lex(String input) {
        return OptionalLexResult.of(this.factory.create().strings().with(this.key, input));
    }

    @Override
    public GenerationResult generate(N node) {
        return new OptionalGenerationResult(node.strings().find(this.key));
    }
}