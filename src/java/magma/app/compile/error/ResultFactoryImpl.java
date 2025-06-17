package magma.app.compile.error;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.context.NodeContext;
import magma.app.compile.context.StringContext;
import magma.app.compile.node.Node;

public class ResultFactoryImpl implements ResultFactory<Node, FormattedError> {
    private ResultFactoryImpl() {
    }

    public static ResultFactory<Node, FormattedError> create() {
        return new ResultFactoryImpl();
    }

    @Override
    public Result<Node, FormattedError> fromStringErr(String message, String input) {
        return new Err<>(new CompileError(message, new StringContext(input)));
    }

    @Override
    public Result<String, FormattedError> fromNodeErr(String message, Node node) {
        return new Err<>(new CompileError(message, new NodeContext(node)));
    }

    @Override
    public Result<Node, FormattedError> fromNode(Node value) {
        return new Ok<>(value);
    }

    @Override
    public Result<String, FormattedError> fromString(String value) {
        return new Ok<>(value);
    }
}
