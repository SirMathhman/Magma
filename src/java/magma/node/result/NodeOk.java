package magma.node.result;

import magma.error.FormattedError;
import magma.node.Node;
import magma.result.Ok;
import magma.result.Result;

import java.util.function.Function;

public record NodeOk(Node node) implements NodeResult {
    @Override
    public <Return> Return match(final Function<Node, Return> whenOk, final Function<FormattedError, Return> whenError) {
        return whenOk.apply(this.node);
    }

    @Override
    public <Return> Result<Return, FormattedError> mapToResult(final Function<Node, Return> mapper) {
        return new Ok<>(mapper.apply(this.node));
    }

    @Override
    public NodeResult map(final Function<Node, Node> mapper) {
        return new NodeOk(mapper.apply(this.node));
    }
}
