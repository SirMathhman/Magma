package magma.app.rule;

import magma.app.MapNode;

public record StringRule(String key) implements Rule {
    @Override
    public RuleResult generate(MapNode mapNode) {
        return new RuleResult(mapNode.findString(this.key));
    }
}