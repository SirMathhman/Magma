package magma.factory;

import magma.node.result.NodeResult;
import magma.string.StringResult;

public interface ResultFactory<Node> {
    NodeResult<Node> fromNode(Node node);

    StringResult fromStringError(String message, Node node);

    StringResult fromString(String value);
}
