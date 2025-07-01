package magma.node.result;

public interface NodeListResult<NodeResult> {
    NodeListResult<NodeResult> add(NodeResult other);

    NodeResult toNode(String key);
}