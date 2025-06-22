package magma.rule;

import magma.error.CompileError;
import magma.error.NodeContext;
import magma.node.EverythingNode;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.string.StringErr;
import magma.string.StringOk;
import magma.string.StringResult;

public class SimpleResultFactory implements ResultFactory {
    @Override
    public NodeResult<EverythingNode> fromNode(final EverythingNode node) {
        return new NodeOk<>(node);
    }

    @Override
    public StringResult fromStringError(final String message, final EverythingNode node) {
        return new StringErr(new CompileError(message, new NodeContext(node)));
    }

    @Override
    public StringResult fromString(final String value) {
        return new StringOk(value);
    }
}