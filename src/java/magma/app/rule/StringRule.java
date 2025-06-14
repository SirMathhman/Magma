package magma.app.rule;

import magma.app.node.MapNode;
import magma.app.node.Node;
import magma.app.rule.result.GenerationResult;
import magma.app.rule.result.LexResult;

public record StringRule(String key) implements Rule {
    @Override
    public LexResult lex(String input) {
        return LexResult.of(new MapNode().withString(this.key, input));
    }

    @Override
    public GenerationResult generate(Node node) {
        return new GenerationResult(node.findString(this.key));
    }
}