package magma.app.compile.node.result;

import java.util.function.Function;

public record NodeErr<Node, Error>(Error error) implements NodeResult<Node, Error> {
    @Override
    public <Return> Return match(final Function<Node, Return> whenOk, final Function<Error, Return> whenError) {
        return whenError.apply(error);
    }

    @Override
    public NodeResult<Node, Error> map(final Function<Node, Node> mapper) {
        return new NodeErr<>(error);
    }
}
