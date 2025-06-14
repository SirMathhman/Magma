package magma.app.rule;

import magma.app.MapNode;

public interface Rule {
    RuleResult generate(MapNode mapNode);
}
