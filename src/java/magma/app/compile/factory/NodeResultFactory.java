package magma.app.compile.factory;

public interface NodeResultFactory<Node, NodeResult> {
    NodeResult fromNode(Node node);

    NodeResult fromNodeError(String message, String context);
}
