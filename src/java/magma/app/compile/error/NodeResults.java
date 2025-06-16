package magma.app.compile.error;

import magma.app.compile.context.StringContext;
import magma.app.compile.node.NodeWithEverything;

import java.util.List;

public class NodeResults {
    public static NodeResult<NodeWithEverything, FormattedError, StringResult<FormattedError>> Ok(NodeWithEverything node) {
        return new NodeOk<>(node);
    }

    public static NodeResult<NodeWithEverything, FormattedError, StringResult<FormattedError>> Err(FormattedError error) {
        return new NodeErr<>(error);
    }

    static NodeResult<NodeWithEverything, FormattedError, StringResult<FormattedError>> ErrWithString(String message, String context) {
        return Err(new CompileError(message, new StringContext(context)));
    }

    static NodeResult<NodeWithEverything, FormattedError, StringResult<FormattedError>> ErrWithChildren(String message, String context, List<FormattedError> errors) {
        return Err(new CompileError(message, new StringContext(context), errors));
    }
}
