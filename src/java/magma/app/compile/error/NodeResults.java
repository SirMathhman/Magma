package magma.app.compile.error;

import magma.api.Error;
import magma.app.compile.context.StringContext;
import magma.app.compile.error.node.NodeErr;
import magma.app.compile.error.node.NodeOk;
import magma.app.compile.node.NodeWithEverything;

import java.util.List;

public class NodeResults {
    public static NodeResult<NodeWithEverything> Ok(NodeWithEverything node) {
        return new NodeOk(node);
    }

    public static NodeResult<NodeWithEverything> Err(Error error) {
        return new NodeErr(error);
    }

    static NodeResult<NodeWithEverything> ErrWithString(String message, String context) {
        return Err(new CompileError(message, new StringContext(context)));
    }

    static NodeResult<NodeWithEverything> ErrWithChildren(String message, String context, List<Error> errors) {
        return Err(new CompileError(message, new StringContext(context), errors));
    }
}
