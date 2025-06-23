package magma.app.compile.node.factory;

public interface NodeResultFactory<Node, NodeResult> {
    NodeResult fromNode(Node node);

    NodeResult fromNodeError(String message, String context);
}
