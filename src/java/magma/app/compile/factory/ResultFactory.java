package magma.app.compile.factory;

public interface ResultFactory<Node, NodeResult, StringResult, ErrorList> extends StringResultFactory<Node, StringResult, ErrorList> {
    NodeResult fromNode(Node node);

    NodeResult fromNodeError(String message, String context);

    NodeResult fromNodeErrorWithChildren(String message, String context, ErrorList errors);
}