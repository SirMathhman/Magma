package magma.app.compile.error;

import magma.app.compile.context.NodeContext;
import magma.app.compile.context.StringContext;
import magma.app.compile.error.node.NodeErr;
import magma.app.compile.error.node.NodeOk;
import magma.app.compile.error.node.NodeResult;
import magma.app.compile.error.string.StringErr;
import magma.app.compile.error.string.StringOk;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.node.Node;

import java.util.List;

public class ResultFactoryImpl implements ResultFactory<Node, FormattedError, NodeResult<Node>, StringResult> {
    private ResultFactoryImpl() {
    }

    public static ResultFactory<Node, FormattedError, NodeResult<Node>, StringResult> create() {
        return new ResultFactoryImpl();
    }

    @Override
    public NodeResult<Node> fromStringErr(String message, String input) {
        return new NodeErr(new CompileError(message, new StringContext(input)));
    }

    @Override
    public StringResult fromNodeErr(String message, Node node) {
        return new StringErr(new CompileError(message, new NodeContext(node)));
    }

    @Override
    public NodeResult<Node> fromNode(Node value) {
        return new NodeOk(value);
    }

    @Override
    public StringResult fromString(String value) {
        return new StringOk(value);
    }

    @Override
    public NodeResult<Node> fromStringErrWithChildren(String message, String input, List<FormattedError> errors) {
        return new NodeErr(new CompileError(message, new StringContext(input), errors));
    }

    @Override
    public StringResult fromNodeErrWithChildren(String message, Node node, List<FormattedError> errors) {
        return new StringErr(new CompileError(message, new NodeContext(node), errors));
    }
}
