package magma.compile.result;

import magma.error.CompileError;
import magma.error.FormatError;
import magma.node.EverythingNode;
import magma.node.TypedNode;
import magma.string.result.StringErr;
import magma.string.result.StringResult;

import java.util.List;

public class ResultFactoryImpl implements ResultFactory<StringResult<FormatError>> {
    private ResultFactoryImpl() {
    }

    public static ResultFactory<StringResult<FormatError>> createResultFactory() {
        return new ResultFactoryImpl();
    }

    @Override
    public <Node extends TypedNode<Node>> StringResult<FormatError> create(final String message, final Node node) {
        return new StringErr<>(new CompileError(message, node.toString()));
    }

    @Override
    public StringResult<FormatError> createWithChildren(final String message,
                                                        final EverythingNode node,
                                                        final List<FormatError> errors) {
        return new StringErr<>(new CompileError(message, node.toString(), errors));
    }
}
