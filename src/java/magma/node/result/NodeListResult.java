package magma.node.result;

public interface NodeListResult<Node, Error> {
    NodeListResult<Node, Error> add(NodeResult<Node, Error> other);

    NodeResult<Node, Error> toNode(String key);
}
