package magma.app.compile;

import java.util.function.Supplier;

public sealed interface NodeListResult<Node, NodeResult> permits NodeListErr, NodeListOk {
    NodeResult toNode(NodeFactory<Node> factory, String key);

    NodeListResult<Node, NodeResult> add(Supplier<NodeResult> action);
}