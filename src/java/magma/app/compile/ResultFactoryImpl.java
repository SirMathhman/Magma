package magma.app.compile;

import magma.api.collect.iter.Iterable;

public class ResultFactoryImpl implements ResultFactory<Node, FormattedError, NodeResult<Node, FormattedError, Iterable<FormattedError>>, StringResult<FormattedError, Iterable<FormattedError>>> {
    private ResultFactoryImpl() {
    }

    public static ResultFactory<Node, FormattedError, NodeResult<Node, FormattedError, Iterable<FormattedError>>, StringResult<FormattedError, Iterable<FormattedError>>> create() {
        return new ResultFactoryImpl();
    }

    @Override
    public NodeResult<Node, FormattedError, Iterable<FormattedError>> fromStringErr(String message, String input) {
        return new NodeErr<>(new CompileError(message, new StringContext(input)));
    }

    @Override
    public StringResult<FormattedError, Iterable<FormattedError>> fromNodeErr(String message, Node node) {
        return new StringErr<>(new CompileError(message, new NodeContext(node)));
    }

    @Override
    public NodeResult<Node, FormattedError, Iterable<FormattedError>> fromNode(Node value) {
        return new NodeOk<>(value);
    }

    @Override
    public StringResult<FormattedError, Iterable<FormattedError>> fromString(String value) {
        return new StringOk<>(value);
    }

    @Override
    public NodeResult<Node, FormattedError, Iterable<FormattedError>> fromStringErrWithChildren(String message, String input, Iterable<FormattedError> errors) {
        return new NodeErr<>(new CompileError(message, new StringContext(input), errors));
    }

    @Override
    public StringResult<FormattedError, Iterable<FormattedError>> fromNodeErrWithChildren(String message, Node node, Iterable<FormattedError> errors) {
        return new StringErr<>(new CompileError(message, new NodeContext(node), errors));
    }

    @Override
    public NodeListResult<Node, NodeResult<Node, FormattedError, Iterable<FormattedError>>> fromEmptyNodeList() {
        return new NodeListOk<>();
    }

    @Override
    public StringResult<FormattedError, Iterable<FormattedError>> fromEmptyString() {
        return new StringOk<>();
    }
}
