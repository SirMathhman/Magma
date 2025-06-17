package magma.app.compile;

import java.util.function.Supplier;

public interface NodeListResult<Node, NodeResult> {
    NodeResult toNode(NodeFactory<Node> factory, String key);

    NodeListResult<Node, NodeResult> add(Supplier<NodeResult> action);
}