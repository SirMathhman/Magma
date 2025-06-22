package magma.app.compile.factory;

public interface ResultFactory<Node, NodeResult, StringResult, ErrorList> {
    NodeResult fromNode(Node node);

    NodeResult fromNodeError(String message, String context);

    NodeResult fromNodeErrorWithChildren(String message, String context, ErrorList errors);

    StringResult fromString(String value);

    StringResult fromStringError(String message, Node node);

    StringResult fromStringErrorWithChildren(String message, Node context, ErrorList errors);
}