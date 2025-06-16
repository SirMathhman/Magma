package magma.app.compile.error;

import magma.app.compile.context.NodeContext;
import magma.app.compile.node.NodeWithEverything;

import java.util.List;

public class StringResults {
    public static StringResult<FormattedError> Ok(String value) {
        return new StringOk<>(value);
    }

    public static StringResult<FormattedError> Err(FormattedError error) {
        return new StringErr<>(error);
    }

    static StringResult<FormattedError> ErrWithChildren(String message, NodeWithEverything node, List<FormattedError> errors) {
        return Err(new CompileError(message, new NodeContext(node), errors));
    }

    static StringResult<FormattedError> ErrWithNodes(String message, NodeWithEverything context) {
        return Err(new CompileError(message, new NodeContext(context)));
    }
}
