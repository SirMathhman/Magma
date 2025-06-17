package magma.app.compile.error;

public interface ResultFactory<Node, NodeResult, StringResult> {
    NodeResult fromStringErr(String message, String input);

    StringResult fromNodeErr(String message, Node node);

    NodeResult fromNode(Node value);

    StringResult fromString(String value);
}
