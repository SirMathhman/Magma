package magma.factory;

public interface ResultFactory<Node, NodeResult, StringResult> {
    NodeResult fromNode(Node node);

    StringResult fromStringError(String message, Node node);

    StringResult fromString(String value);
}
