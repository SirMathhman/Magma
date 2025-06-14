package magma.app.rule;

import magma.app.node.Node;
import magma.app.rule.result.GenerationResult;

public interface Rule {
    GenerationResult generate(Node node);
}
