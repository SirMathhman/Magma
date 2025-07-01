package magma.node.result;

public interface NodeListResult<Node> {
    NodeListResult<Node> add(NodeResult<Node> other);

    NodeResult<Node> toNode(String key);
}
