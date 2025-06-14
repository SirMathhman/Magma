package magma.app.rule;

import magma.app.MapNode;
import magma.app.rule.result.GenerationResult;

public record SuffixRule(Rule rule, String suffix) implements Rule {
    @Override
    public GenerationResult generate(MapNode node) {
        return new GenerationResult(this.rule.generate(node).value().map(value -> value + this.suffix));
    }
}