package magma.app.compile.error;

import magma.api.Error;
import magma.app.compile.context.NodeContext;
import magma.app.compile.error.string.StringErr;
import magma.app.compile.error.string.StringOk;
import magma.app.compile.node.NodeWithEverything;

import java.util.List;

public class StringResults {
    public static StringResult Ok(String value) {
        return new StringOk(value);
    }

    public static StringResult Err(Error error) {
        return new StringErr(error);
    }

    static StringResult ErrWithChildren(String message, NodeWithEverything node, List<Error> errors) {
        return Err(new CompileError(message, new NodeContext(node), errors));
    }

    static StringResult ErrWithNodes(String message, NodeWithEverything context) {
        return Err(new CompileError(message, new NodeContext(context)));
    }
}
