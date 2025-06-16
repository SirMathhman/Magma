package magma.app.compile.error;

import magma.app.compile.context.NodeContext;
import magma.app.compile.context.StringContext;
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
        return new NodeOk(node);
    }

    @Override
    public StringResult fromString(String value) {
        return new StringOk(value);
    }

    @Override
    public StringResult fromNodeError(String message, NodeWithEverything context) {
        return new StringErr(new CompileError(message, new NodeContext(context)));
    }

    @Override
    public NodeResult<NodeWithEverything> fromStringError(String message, String context) {
        return new NodeErr(new CompileError(message, new StringContext(context)));
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
    public NodeResult<NodeWithEverything> fromStringErrorWithChildren(String message, String context, List<CompileError> errors) {
        return new NodeErr(new CompileError(message, new StringContext(context), errors));
    }

    @Override
    public StringResult fromNodeErrorWithChildren(String message, NodeWithEverything node, List<CompileError> errors) {
        return new StringErr(new CompileError(message, new NodeContext(node), errors));
    }
}
