package magma.app.compile;

import magma.app.compile.context.NodeContext;
import magma.app.compile.context.StringContext;
import magma.app.compile.list.NodeListOk;
import magma.app.compile.list.NodeListResult;
import magma.app.compile.node.Node;
import magma.app.compile.node.NodeErr;
import magma.app.compile.node.NodeOk;
import magma.app.compile.node.NodeResult;
import magma.app.compile.string.StringErr;
import magma.app.compile.string.StringOk;
import magma.app.compile.string.StringResult;

import java.util.List;

public class ResultFactoryImpl implements ResultFactory<Node, FormattedError, NodeResult<Node, FormattedError>, StringResult> {
    private ResultFactoryImpl() {
    }

    public static ResultFactory<Node, FormattedError, NodeResult<Node, FormattedError>, StringResult> create() {
        return new ResultFactoryImpl();
    }

    @Override
    public NodeResult<Node, FormattedError> fromStringErr(String message, String input) {
        return new NodeErr(new CompileError(message, new StringContext(input)));
    }

    @Override
    public StringResult fromNodeErr(String message, Node node) {
        return new StringErr(new CompileError(message, new NodeContext(node)));
    }

    @Override
    public NodeResult<Node, FormattedError> fromNode(Node value) {
        return new NodeOk(value);
    }

    @Override
    public StringResult fromString(String value) {
        return new StringOk(value);
    }

    @Override
    public NodeResult<Node, FormattedError> fromStringErrWithChildren(String message, String input, List<FormattedError> errors) {
        return new NodeErr(new CompileError(message, new StringContext(input), errors));
    }

    @Override
    public StringResult fromNodeErrWithChildren(String message, Node node, List<FormattedError> errors) {
        return new StringErr(new CompileError(message, new NodeContext(node), errors));
    }

    @Override
    public NodeListResult<Node, NodeResult<Node, FormattedError>> fromEmptyNodeList() {
        return new NodeListOk();
    }

    @Override
    public StringResult fromEmptyString() {
        return new StringOk();
    }
}
