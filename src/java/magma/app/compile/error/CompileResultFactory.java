package magma.app.compile.error;

public interface CompileResultFactory<Node, StringResult, NodeResult, NodeListResult> {
    NodeResult fromNode(Node node);

    StringResult fromString(String generated);

    StringResult fromNodeError(String message, Node context);

    NodeResult fromStringError(String message, String context);

    NodeListResult fromEmptyNodeList();

    StringResult fromEmptyString();
}