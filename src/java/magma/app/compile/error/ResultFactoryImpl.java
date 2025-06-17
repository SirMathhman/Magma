package magma.app.compile.error;

import magma.app.compile.context.NodeContext;
import magma.app.compile.context.StringContext;
import magma.app.compile.node.Node;

public class ResultFactoryImpl implements ResultFactory<Node, NodeResult, StringResult> {
    private ResultFactoryImpl() {
    }

    public static ResultFactory<Node, NodeResult, StringResult> create() {
        return new ResultFactoryImpl();
    }

    @Override
    public NodeResult fromStringErr(String message, String input) {
        return new NodeErr(new CompileError(message, new StringContext(input)));
    }

    @Override
    public StringResult fromNodeErr(String message, Node node) {
        return new StringErr(new CompileError(message, new NodeContext(node)));
    }

    @Override
    public NodeResult fromNode(Node value) {
        return new NodeOk(value);
    }

    @Override
    public StringResult fromString(String value) {
        return new StringOk(value);
    }
}
