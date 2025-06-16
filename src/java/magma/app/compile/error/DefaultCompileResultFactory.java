package magma.app.compile.error;

import magma.api.Error;
import magma.app.compile.error.list.NodeListOk;
import magma.app.compile.node.NodeWithEverything;

import java.util.List;

public class DefaultCompileResultFactory implements CompileResultFactory<NodeWithEverything, StringResult, NodeResult<NodeWithEverything>, NodeListResult<NodeWithEverything>> {
    private DefaultCompileResultFactory() {
    }

    public static CompileResultFactory<NodeWithEverything, StringResult, NodeResult<NodeWithEverything>, NodeListResult<NodeWithEverything>> createResultCompileResultFactory() {
        return new DefaultCompileResultFactory();
    }

    @Override
    public NodeResult<NodeWithEverything> fromNode(NodeWithEverything node) {
        return NodeResults.Ok(node);
    }

    @Override
    public StringResult fromString(String value) {
        return StringResults.Ok(value);
    }

    @Override
    public StringResult fromNodeError(String message, NodeWithEverything context) {
        return StringResults.ErrWithNodes(message, context);
    }

    @Override
    public NodeResult<NodeWithEverything> fromStringError(String message, String context) {
        return NodeResults.ErrWithString(message, context);
    }

    @Override
    public NodeListResult<NodeWithEverything> fromEmptyNodeList() {
        return new NodeListOk();
    }

    @Override
    public StringResult fromEmptyString() {
        return this.fromString("");
    }

    @Override
    public NodeResult<NodeWithEverything> fromStringErrorWithChildren(String message, String context, List<Error> errors) {
        return NodeResults.ErrWithChildren(message, context, errors);
    }

    @Override
    public StringResult fromNodeErrorWithChildren(String message, NodeWithEverything node, List<Error> errors) {
        return StringResults.ErrWithChildren(message, node, errors);
    }
}
