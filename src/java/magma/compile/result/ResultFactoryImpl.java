package magma.compile.result;

import magma.error.CompileError;
import magma.error.FormatError;
import magma.error.context.StringContext;
import magma.node.EverythingNode;
import magma.node.factory.MapNodeFactory;
import magma.node.result.NodeErr;
import magma.node.result.NodeListOk;
import magma.node.result.NodeListResult;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.rule.StringOk;
import magma.string.result.StringErr;
import magma.string.result.StringResult;

import java.util.List;

public class ResultFactoryImpl implements ResultFactory<EverythingNode, FormatError, StringResult<FormatError>, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>> {
    private ResultFactoryImpl() {
    }

    public static ResultFactory<EverythingNode, FormatError, StringResult<FormatError>, NodeResult<EverythingNode, FormatError, StringResult<FormatError>>> get() {
        return new ResultFactoryImpl();
    }

    @Override
    public NodeListResult<NodeResult<EverythingNode, FormatError, StringResult<FormatError>>> createNodeList() {
        return new NodeListOk<>(new MapNodeFactory());
    }

    @Override
    public StringResult<FormatError> createStringError(final String message, final EverythingNode everythingNode) {
        return new StringErr<>(new CompileError(message, new NodeContext(everythingNode)));
    }

    @Override
    public StringResult<FormatError> createStringErrorWithChildren(final String message,
                                                                   final EverythingNode context,
                                                                   final List<FormatError> errors) {
        return new StringErr<>(new CompileError(message, new NodeContext(context), errors));
    }

    @Override
    public NodeResult<EverythingNode, FormatError, StringResult<FormatError>> createNodeError(final String message,
                                                                                              final String context) {
        return new NodeErr<>(new CompileError(message, new StringContext(context)));
    }

    @Override
    public NodeResult<EverythingNode, FormatError, StringResult<FormatError>> createNodeErrorWithChildren(final String message,
                                                                                                          final String context,
                                                                                                          final List<FormatError> errors) {
        return new NodeErr<>(new CompileError(message, new StringContext(context), errors));
    }

    @Override
    public NodeResult<EverythingNode, FormatError, StringResult<FormatError>> createNode(final EverythingNode everythingNode) {
        return new NodeOk<>(everythingNode);
    }

    @Override
    public StringResult<FormatError> createString(final String value) {
        return new StringOk<>(value);
    }

    @Override
    public StringResult<FormatError> createEmptyString() {
        return new StringOk<>("");
    }
}
