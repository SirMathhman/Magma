package magma.node.result;

import magma.error.FormattedError;

import java.util.function.Function;

public record NodeErr<Node>(FormattedError error) implements NodeResult<Node> {
    @Override
    public <Return> Return match(final Function<Node, Return> whenOk, final Function<FormattedError, Return> whenError) {
        return whenError.apply(this.error);
    }

    @Override
    public NodeResult<Node> map(final Function<Node, Node> mapper) {
        return new NodeErr<>(this.error);
    }
}
