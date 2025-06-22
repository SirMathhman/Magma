package magma.app.node.result;

import java.util.function.Function;

public record NodeOk<Node, Error>(Node node) implements NodeResult<Node, Error> {
    @Override
    public <Return> Return match(final Function<Node, Return> whenOk, final Function<Error, Return> whenError) {
        return whenOk.apply(this.node);
    }

    @Override
    public NodeResult<Node, Error> map(final Function<Node, Node> mapper) {
        return new NodeOk<>(mapper.apply(this.node));
    }
}
