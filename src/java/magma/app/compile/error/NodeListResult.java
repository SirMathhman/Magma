package magma.app.compile.error;

import java.util.function.Supplier;

public interface NodeListResult<Node, NodeResult> {
    NodeResult toNode(String key);

    NodeListResult<Node, NodeResult> add(Supplier<NodeResult> other);
}
