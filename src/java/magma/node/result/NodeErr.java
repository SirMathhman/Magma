package magma.node.result;

import magma.compile.result.ResultFactory;
import magma.string.result.StringResult;

import java.util.List;
import java.util.function.Function;

public record NodeErr<Node, Error>(Error error) implements NodeResult<Node, Error, StringResult<Error>> {

    @Override
    public <Return> Return match(final Function<Node, Return> whenPresent, final Function<Error, Return> whenErr) {
        return whenErr.apply(this.error);
    }

    @Override
    public NodeResult<Node, Error, StringResult<Error>> mapValue(final Function<Node, Node> mapper) {
        return new NodeErr<>(this.error);
    }

    @Override
    public NodeResult<Node, Error, StringResult<Error>> flatMap(final Function<Node, NodeResult<Node, Error, StringResult<Error>>> mapper) {
        return this;
    }

    @Override
    public NodeResult<Node, Error, StringResult<Error>> mapErr(final String message,
                                                               final String context,
                                                               final ResultFactory<Node, Error, StringResult<Error>> factory) {
        return factory.createNodeErrorWithChildren(message, context, List.of(this.error));
    }
}
