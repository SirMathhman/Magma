package magma.app.compile.rule;

import magma.app.compile.node.core.StringNode;
import magma.app.compile.rule.factory.NodeFactory;
import magma.app.compile.rule.result.GenerationResult;
import magma.app.compile.rule.result.LexResult;
import magma.app.compile.rule.result.optional.OptionalGenerationResult;
import magma.app.compile.rule.result.optional.OptionalLexResult;

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