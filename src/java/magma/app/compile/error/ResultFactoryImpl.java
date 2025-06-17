package magma.app.compile.error;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.context.NodeContext;
import magma.app.compile.context.StringContext;
import magma.app.compile.node.DisplayNode;

public class ResultFactoryImpl implements ResultFactory {
    private ResultFactoryImpl() {
    }

    public static ResultFactory create() {
        return new ResultFactoryImpl();
    }

    @Override
    public <Node> Result<Node, FormattedError> fromStringErr(String message, String input) {
        return new Err<>(new CompileError(message, new StringContext(input)));
    }

    @Override
    public <Node extends DisplayNode> Result<String, FormattedError> fromNodeErr(String message, Node node) {
        return new Err<>(new CompileError(message, new NodeContext(node)));
    }

    @Override
    public <Node> Result<Node, FormattedError> fromNode(Node value) {
        return new Ok<>(value);
    }

    @Override
    public Result<String, FormattedError> fromString(String value) {
        return new Ok<>(value);
    }
}
