package magma.app.compile;

import magma.api.list.Iterable;

public class ResultFactoryImpl implements ResultFactory<Node, FormattedError, NodeResult<Node, FormattedError>, StringResult<FormattedError>> {
    private ResultFactoryImpl() {
    }

    public static ResultFactory<Node, FormattedError, NodeResult<Node, FormattedError>, StringResult<FormattedError>> create() {
        return new ResultFactoryImpl();
    }

    @Override
    public NodeResult<Node, FormattedError> fromStringErr(String message, String input) {
        return new NodeErr<>(new CompileError(message, new StringContext(input)));
    }

    @Override
    public StringResult<FormattedError> fromNodeErr(String message, Node node) {
        return new StringErr<>(new CompileError(message, new NodeContext(node)));
    }

    @Override
    public NodeResult<Node, FormattedError> fromNode(Node value) {
        return new NodeOk<>(value);
    }

    @Override
    public StringResult<FormattedError> fromString(String value) {
        return new StringOk<>(value);
    }

    @Override
    public NodeResult<Node, FormattedError> fromStringErrWithChildren(String message, String input, Iterable<FormattedError> errors) {
        return new NodeErr<>(new CompileError(message, new StringContext(input), errors));
    }

    @Override
    public StringResult<FormattedError> fromNodeErrWithChildren(String message, Node node, Iterable<FormattedError> errors) {
        return new StringErr<>(new CompileError(message, new NodeContext(node), errors));
    }

    @Override
    public NodeListResult<Node, NodeResult<Node, FormattedError>> fromEmptyNodeList() {
        return new NodeListOk<>();
    }

    @Override
    public StringResult<FormattedError> fromEmptyString() {
        return new StringOk<>();
    }
}
