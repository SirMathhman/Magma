package magma.app.compile.error;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.context.NodeContext;
import magma.app.compile.context.StringContext;
import magma.app.compile.node.DisplayNode;

public class CompileErrors {
    public static <Node> Result<Node, FormattedError> fromStringErr(String message, String input) {
        return new Err<>(new CompileError(message, new StringContext(input)));
    }

    public static <Node extends DisplayNode> Result<String, FormattedError> fromNodeErr(String message, Node node) {
        return new Err<>(new CompileError(message, new NodeContext(node)));
    }

    public static <Node> Result<Node, FormattedError> fromNode(Node value) {
        return new Ok<>(value);
    }

    public static Result<String, FormattedError> fromString(String value) {
        return new Ok<>(value);
    }
}
