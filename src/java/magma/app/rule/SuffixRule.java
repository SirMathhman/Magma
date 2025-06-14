package magma.app.rule;

import magma.app.node.Node;
import magma.app.rule.result.GenerationResult;

public record SuffixRule(Rule rule, String suffix) implements Rule {
    @Override
    public GenerationResult generate(Node node) {
        return new GenerationResult(this.rule.generate(node).value().map(value -> value + this.suffix));
    }
}