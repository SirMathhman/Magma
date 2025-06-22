package magma.factory;

import magma.error.CompileError;
import magma.error.FormattedError;
import magma.error.NodeContext;
import magma.error.StringContext;
import magma.list.ListLike;
import magma.node.DisplayNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.string.StringErr;
import magma.string.StringOk;
import magma.string.StringResult;

public class SimpleResultFactory<Node extends DisplayNode> implements ResultFactory<Node, NodeResult<Node>, StringResult> {
    @Override
    public NodeResult<Node> fromNode(final Node node) {
        return new NodeOk<>(node);
    }

    @Override
    public StringResult fromStringError(final String message, final Node node) {
        return new StringErr(new CompileError(message, new NodeContext(node)));
    }

    @Override
    public StringResult fromString(final String value) {
        return new StringOk(value);
    }

    @Override
    public NodeResult<Node> fromNodeErrorWithChildren(final String message, final String context, final ListLike<FormattedError> errors) {
        return new NodeErr<>(new CompileError(message, new StringContext(context), errors));
    }

    @Override
    public StringResult fromStringErrorWithChildren(final String message, final Node context, final ListLike<FormattedError> errors) {
        return new StringErr(new CompileError(message, new NodeContext(context), errors));
    }
}