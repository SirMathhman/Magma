package magma.app.compile.node.factory;

public interface ParentNodeResultFactory<Node, NodeResult, ErrorList> extends NodeResultFactory<Node, NodeResult> {
    NodeResult fromNodeErrorWithChildren(String message, String context, ErrorList errors);
}
