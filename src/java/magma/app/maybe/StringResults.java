package magma.app.maybe;

import magma.app.CompileError;
import magma.app.maybe.string.ErrStringResult;
import magma.app.maybe.string.OkStringResult;
import magma.app.rule.NodeContext;

import java.util.List;

public class StringResults {
    public static <Node> StringResult<CompileError> createFromNodeAndErrors(String message, Node node, List<CompileError> errors) {
        return new ErrStringResult<>(new CompileError(message, new NodeContext<>(node), errors));
    }

    public static <Error> StringResult<Error> createFromValue(String value) {
        return new OkStringResult<>(value);
    }
}
