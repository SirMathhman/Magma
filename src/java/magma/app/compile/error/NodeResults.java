package magma.app.compile.error;

import magma.app.compile.context.StringContext;
import magma.app.compile.node.NodeWithEverything;

import java.util.List;

public class NodeResults {
    public static NodeResult<NodeWithEverything> Ok(NodeWithEverything node) {
        return new NodeOk(node);
    }

    public static NodeResult<NodeWithEverything> Err(FormattedError error) {
        return new NodeErr(error);
    }

    static NodeResult<NodeWithEverything> ErrWithString(String message, String context) {
        return Err(new CompileError(message, new StringContext(context)));
    }

    static NodeResult<NodeWithEverything> ErrWithChildren(String message, String context, List<FormattedError> errors) {
        return Err(new CompileError(message, new StringContext(context), errors));
    }
}
