package magma.app.rule;

import magma.app.node.core.StringNode;
import magma.app.node.properties.PropertiesCompoundNode;
import magma.app.rule.result.GenerationResult;
import magma.app.rule.result.LexResult;
import magma.app.rule.result.optional.OptionalGenerationResult;
import magma.app.rule.result.optional.OptionalLexResult;

public final class StringRule<N extends StringNode<N>> implements Rule<N> {
    private final String key;

    public StringRule(String key) {
        this.key = key;
    }

    @Override
    public LexResult lex(String input) {
        return OptionalLexResult.of(new PropertiesCompoundNode().strings().with(this.key, input));
    }

    @Override
    public GenerationResult generate(N node) {
        return new OptionalGenerationResult(node.strings().find(this.key));
    }
}