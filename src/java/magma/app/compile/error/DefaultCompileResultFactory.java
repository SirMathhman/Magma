package magma.app.compile.error;

import magma.app.compile.node.NodeWithEverything;

import java.util.List;

public class DefaultCompileResultFactory implements CompileResultFactory<NodeWithEverything, FormattedError, StringResult<FormattedError>, NodeResult<NodeWithEverything, FormattedError>, NodeListResult<NodeWithEverything, FormattedError>> {
    private DefaultCompileResultFactory() {
    }

    public static CompileResultFactory<NodeWithEverything, FormattedError, StringResult<FormattedError>, NodeResult<NodeWithEverything, FormattedError>, NodeListResult<NodeWithEverything, FormattedError>> create() {
        return new DefaultCompileResultFactory();
    }

    @Override
    public NodeResult<NodeWithEverything, FormattedError> fromNode(NodeWithEverything node) {
        return NodeResults.Ok(node);
    }

    @Override
    public StringResult<FormattedError> fromString(String value) {
        return StringResults.Ok(value);
    }

    @Override
    public StringResult<FormattedError> fromNodeError(String message, NodeWithEverything context) {
        return StringResults.ErrWithNodes(message, context);
    }

    @Override
    public NodeResult<NodeWithEverything, FormattedError> fromStringError(String message, String context) {
        return NodeResults.ErrWithString(message, context);
    }

    @Override
    public NodeListResult<NodeWithEverything, FormattedError> fromEmptyNodeList() {
        return new NodeListOk<>();
    }

    @Override
    public StringResult<FormattedError> fromEmptyString() {
        return this.fromString("");
    }

    @Override
    public NodeResult<NodeWithEverything, FormattedError> fromStringErrorWithChildren(String message, String context, List<FormattedError> errors) {
        return NodeResults.ErrWithChildren(message, context, errors);
    }

    @Override
    public StringResult<FormattedError> fromNodeErrorWithChildren(String message, NodeWithEverything node, List<FormattedError> errors) {
        return StringResults.ErrWithChildren(message, node, errors);
    }
}
