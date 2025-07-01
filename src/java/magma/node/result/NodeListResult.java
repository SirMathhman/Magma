package magma.node.result;

import magma.error.FormatError;

public interface NodeListResult<Node> {
    NodeListResult<Node> add(NodeResult<Node, FormatError> other);

    NodeResult<Node, FormatError> toNode(String key);
}
