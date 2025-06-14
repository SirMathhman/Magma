package magma.app.rule;

import magma.app.node.properties.PropertiesNode;
import magma.app.node.Node;
import magma.app.rule.result.GenerationResult;
import magma.app.rule.result.optional.OptionalGenerationResult;
import magma.app.rule.result.LexResult;
import magma.app.rule.result.optional.OptionalLexResult;

public record StringRule(String key) implements Rule {
    @Override
    public LexResult lex(String input) {
        Node node = new PropertiesNode();
        return OptionalLexResult.of(node.strings().with(this.key, input));
    }

    @Override
    public GenerationResult generate(Node node) {
        return new OptionalGenerationResult(node.strings().find(this.key));
    }
}