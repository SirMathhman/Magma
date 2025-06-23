package magma.app.compile.factory;

public interface NodeResultFactory<Node, NodeResult, ErrorList> {
    NodeResult fromNode(Node node);

    NodeResult fromNodeErrorWithChildren(String message, String context, ErrorList errors);

    NodeResult fromNodeError(String message, String context);
}
