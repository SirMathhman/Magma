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

public class SimpleResultFactory<Node extends DisplayNode> implements ResultFactory<Node, FormattedError, NodeResult<Node, FormattedError>, StringResult<FormattedError>> {
    @Override
    public NodeResult<Node, FormattedError> fromNode(final Node node) {
        return new NodeOk<>(node);
    }

    @Override
    public NodeResult<Node, FormattedError> fromNodeError(final String message, final String context) {
        return new NodeErr<>(new CompileError(message, new StringContext(context)));
    }

    @Override
    public StringResult<FormattedError> fromStringError(final String message, final Node node) {
        return new StringErr<>(new CompileError(message, new NodeContext(node)));
    }

    @Override
    public StringResult<FormattedError> fromString(final String value) {
        return new StringOk<>(value);
    }

    @Override
    public NodeResult<Node, FormattedError> fromNodeErrorWithChildren(final String message, final String context, final ListLike<FormattedError> errors) {
        return new NodeErr<>(new CompileError(message, new StringContext(context), errors));
    }

    @Override
    public StringResult<FormattedError> fromStringErrorWithChildren(final String message, final Node context, final ListLike<FormattedError> errors) {
        return new StringErr<>(new CompileError(message, new NodeContext(context), errors));
    }
}