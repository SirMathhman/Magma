package magma.app.rule;

import magma.app.node.MapNode;
import magma.app.node.Node;
import magma.app.rule.result.GenerationResult;
import magma.app.rule.result.optional.OptionalGenerationResult;
import magma.app.rule.result.LexResult;
import magma.app.rule.result.optional.OptionalLexResult;

public record StringRule(String key) implements Rule {
    @Override
    public LexResult lex(String input) {
        return OptionalLexResult.of(new MapNode().withString(this.key, input));
    }

    @Override
    public GenerationResult generate(Node node) {
        return new OptionalGenerationResult(node.findString(this.key));
    }
}