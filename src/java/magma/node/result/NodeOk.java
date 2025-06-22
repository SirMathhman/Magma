package magma.node.result;

import magma.error.FormattedError;

import java.util.function.Function;

public record NodeOk<Node>(Node node) implements NodeResult<Node, FormattedError> {
    @Override
    public <Return> Return match(final Function<Node, Return> whenOk, final Function<FormattedError, Return> whenError) {
        return whenOk.apply(this.node);
    }

    @Override
    public NodeResult<Node, FormattedError> map(final Function<Node, Node> mapper) {
        return new NodeOk<>(mapper.apply(this.node));
    }
}
