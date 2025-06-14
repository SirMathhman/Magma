package magma.app.rule;

import magma.app.MapNode;
import magma.app.rule.result.GenerationResult;

public interface Rule {
    GenerationResult generate(MapNode mapNode);
}
