package magma.node.result;

import magma.error.FormattedError;
import magma.result.Ok;
import magma.result.Result;

import java.util.function.Function;

public final class NodeOk<Node> implements NodeResult<Node> {
    private final Node node;

    public NodeOk(final Node node) {
        this.node = node;
    }

    @Override
    public <Return> Return match(final Function<Node, Return> whenOk, final Function<FormattedError, Return> whenError) {
        return whenOk.apply(this.node);
    }

    @Override
    public <Return> Result<Return, FormattedError> mapToResult(final Function<Node, Return> mapper) {
        return new Ok<>(mapper.apply(this.node));
    }

    @Override
    public NodeResult<Node> map(final Function<Node, Node> mapper) {
        return new NodeOk<Node>(mapper.apply(this.node));
    }
}
