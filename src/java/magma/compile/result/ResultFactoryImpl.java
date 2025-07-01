package magma.compile.result;

import magma.error.CompileError;
import magma.error.FormatError;
import magma.node.EverythingNode;
import magma.node.result.NodeErr;
import magma.node.result.NodeResult;
import magma.string.result.StringErr;
import magma.string.result.StringResult;

import java.util.List;

public class ResultFactoryImpl implements ResultFactory<EverythingNode, StringResult<FormatError>> {
    private ResultFactoryImpl() {
    }

    public static ResultFactory<EverythingNode, StringResult<FormatError>> get() {
        return new ResultFactoryImpl();
    }


    @Override
    public StringResult<FormatError> createStringError(final String message, final EverythingNode everythingNode) {
        return new StringErr<>(new CompileError(message, everythingNode.toString()));
    }

    @Override
    public StringResult<FormatError> createStringErrorWithChildren(final String message,
                                                                   final EverythingNode context,
                                                                   final List<FormatError> errors) {
        return new StringErr<>(new CompileError(message, context.toString(), errors));
    }

    @Override
    public NodeResult<EverythingNode> createNodeError(final String message, final String context) {
        return new NodeErr<>(new CompileError(message, context));
    }
}
