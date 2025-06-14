package magma.app.rule;

import magma.app.MapNode;
import magma.app.rule.result.GenerationResult;
import magma.app.rule.result.LexResult;

import java.util.Optional;

public record StringRule(String key) implements Rule {
    public LexResult lex(String input) {
        return new LexResult(Optional.of(new MapNode().withString(this.key, input)));
    }

    @Override
    public GenerationResult generate(MapNode mapNode) {
        return new GenerationResult(mapNode.findString(this.key));
    }
}