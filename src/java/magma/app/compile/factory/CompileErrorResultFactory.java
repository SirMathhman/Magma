package magma.app.compile.factory;

import magma.app.compile.context.Context;
import magma.app.compile.node.result.NodeErr;
import magma.app.compile.node.result.NodeOk;
import magma.app.compile.node.result.NodeResult;
import magma.app.compile.string.StringErr;
import magma.app.compile.string.StringOk;
import magma.app.compile.string.StringResult;

public class CompileErrorResultFactory<Node, FormattedError, ErrorList> implements ResultFactory<Node, NodeResult<Node, FormattedError>, StringResult<FormattedError>, ErrorList> {
    private final ContextFactory<Node> contextFactory;
    private final ErrorFactory<Context, FormattedError, ErrorList> errorFactory;

    public CompileErrorResultFactory(final ContextFactory<Node> contextFactory, final ErrorFactory<Context, FormattedError, ErrorList> errorFactory) {
        this.contextFactory = contextFactory;
        this.errorFactory = errorFactory;
    }

    @Override
    public NodeResult<Node, FormattedError> fromNode(final Node node) {
        return new NodeOk<>(node);
    }

    @Override
    public NodeResult<Node, FormattedError> fromNodeError(final String message, final String context) {
        return new NodeErr<>(this.errorFactory.createError(message, this.contextFactory.createStringContext(context)));
    }

    @Override
    public StringResult<FormattedError> fromStringError(final String message, final Node node) {
        return new StringErr<>(this.errorFactory.createError(message, this.contextFactory.createNodeContext(node)));
    }

    @Override
    public StringResult<FormattedError> fromString(final String value) {
        return new StringOk<>(value);
    }

    @Override
    public NodeResult<Node, FormattedError> fromNodeErrorWithChildren(final String message, final String context, final ErrorList errors) {
        return new NodeErr<>(this.errorFactory.createErrorWithChildren(message,
                this.contextFactory.createStringContext(context),
                errors));
    }

    @Override
    public StringResult<FormattedError> fromStringErrorWithChildren(final String message, final Node context, final ErrorList errors) {
        return new StringErr<>(this.errorFactory.createErrorWithChildren(message,
                this.contextFactory.createNodeContext(context),
                errors));
    }
}