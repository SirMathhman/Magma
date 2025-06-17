package magma.app.compile.error;

import magma.api.result.Result;
import magma.app.compile.node.DisplayNode;

public interface ResultFactory {
    <Node> Result<Node, FormattedError> fromStringErr(String message, String input);

    <Node extends DisplayNode> Result<String, FormattedError> fromNodeErr(String message, Node node);

    <Node> Result<Node, FormattedError> fromNode(Node value);

    Result<String, FormattedError> fromString(String value);
}
