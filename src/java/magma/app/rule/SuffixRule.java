package magma.app.rule;

import magma.app.MapNode;

public record SuffixRule(Rule rule, String suffix) implements Rule {
    @Override
    public RuleResult generate(MapNode node) {
        return new RuleResult(this.rule.generate(node).value().map(value -> value + this.suffix));
    }
}