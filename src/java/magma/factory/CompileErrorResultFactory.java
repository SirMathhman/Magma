package magma.factory;

import magma.error.CompileError;
import magma.error.ErrorList;
import magma.error.FormattedError;
import magma.node.DisplayNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.string.StringErr;
import magma.string.StringOk;
import magma.string.StringResult;

public class CompileErrorResultFactory<Node extends DisplayNode> implements ResultFactory<Node, FormattedError, NodeResult<Node, FormattedError>, StringResult<FormattedError>> {
    private final ContextFactory<Node> contextFactory;

    public CompileErrorResultFactory(final ContextFactory<Node> contextFactory) {
        this.contextFactory = contextFactory;
    }

    @Override
    public NodeResult<Node, FormattedError> fromNode(final Node node) {
        return new NodeOk<>(node);
    }

    @Override
    public NodeResult<Node, FormattedError> fromNodeError(final String message, final String context) {
        return new NodeErr<>(new CompileError(message, this.contextFactory.createStringContext(context)));
    }

    @Override
    public StringResult<FormattedError> fromStringError(final String message, final Node node) {
        return new StringErr<>(new CompileError(message, this.contextFactory.createNodeContext(node)));
    }

    @Override
    public StringResult<FormattedError> fromString(final String value) {
        return new StringOk<>(value);
    }

    @Override
    public NodeResult<Node, FormattedError> fromNodeErrorWithChildren(final String message, final String context, final ErrorList<FormattedError> errors) {
        return new NodeErr<>(new CompileError(message, this.contextFactory.createStringContext(context), errors));
    }

    @Override
    public StringResult<FormattedError> fromStringErrorWithChildren(final String message, final Node context, final ErrorList<FormattedError> errors) {
        return new StringErr<>(new CompileError(message, this.contextFactory.createNodeContext(context), errors));
    }
}