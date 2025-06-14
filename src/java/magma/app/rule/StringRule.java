package magma.app.rule;

import magma.app.node.CompoundNode;
import magma.app.node.properties.PropertiesCompoundNode;
import magma.app.rule.result.GenerationResult;
import magma.app.rule.result.LexResult;
import magma.app.rule.result.optional.OptionalGenerationResult;
import magma.app.rule.result.optional.OptionalLexResult;

public record StringRule(String key) implements Rule<CompoundNode> {
    @Override
    public LexResult lex(String input) {
        CompoundNode node = new PropertiesCompoundNode();
        return OptionalLexResult.of(node.strings().with(this.key, input));
    }

    @Override
    public GenerationResult generate(CompoundNode node) {
        return new OptionalGenerationResult(node.strings().find(this.key));
    }
}