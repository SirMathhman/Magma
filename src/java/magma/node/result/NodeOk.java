package magma.node.result;

import java.util.function.Function;

public record NodeOk<Node, Error>(Node node) implements NodeResult<Node, Error> {
    @Override
    public <Return> Return match(final Function<Node, Return> whenPresent, final Function<Error, Return> whenErr) {
        return whenPresent.apply(this.node);
    }

    @Override
    public NodeResult<Node, Error> mapValue(final Function<Node, Node> mapper) {
        return new NodeOk<>(mapper.apply(this.node));
    }

    @Override
    public NodeResult<Node, Error> flatMap(final Function<Node, NodeResult<Node, Error>> mapper) {
        return mapper.apply(this.node);
    }

    @Override
    public NodeResult<Node, Error> mapErr(final String message, final String context) {
        return this;
    }
}
