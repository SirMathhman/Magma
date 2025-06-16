package magma.app.compile.error;

import java.util.function.Supplier;

public interface NodeListResult<Node, Error, NodeResult> {
    NodeResult toNode(String key);

    NodeListResult<Node, Error, NodeResult> add(Supplier<NodeResult> other);
}
