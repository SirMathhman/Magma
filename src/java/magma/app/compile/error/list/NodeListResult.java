package magma.app.compile.error.list;

import magma.app.compile.error.node.NodeResult;

import java.util.function.Supplier;

public interface NodeListResult<Node> {
    NodeResult<Node> toNode(String key);

    NodeListResult<Node> add(Supplier<NodeResult<Node>> other);
}
