package magma.compile.result;

import magma.error.CompileError;
import magma.error.FormatError;
import magma.node.EverythingNode;
import magma.node.TypedNode;
import magma.string.result.StringErr;
import magma.string.result.StringResult;

import java.util.List;

public class ResultFactory {
    public static <Node extends TypedNode<Node>> StringResult<FormatError> create(final String message,
                                                                                  final Node node) {
        return new StringErr<>(new CompileError(message, node.toString()));
    }

    public static StringErr<FormatError> createWithChildren(final String message,
                                                            final EverythingNode node,
                                                            final List<FormatError> errors) {
        return new StringErr<>(new CompileError(message, node.toString(), errors));
    }
}
