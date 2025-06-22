package magma.node.result;

import magma.error.FormattedError;
import magma.node.Node;
import magma.result.Err;
import magma.result.Result;

import java.util.function.Function;

public record NodeErr(FormattedError error) implements NodeResult {
    @Override
    public <Return> Return match(final Function<Node, Return> whenOk, final Function<FormattedError, Return> whenError) {
        return whenError.apply(this.error);
    }

    @Override
    public <Return> Result<Return, FormattedError> mapToResult(final Function<Node, Return> mapper) {
        return new Err<>(this.error);
    }

    @Override
    public NodeResult map(final Function<Node, Node> mapper) {
        return new NodeErr(this.error);
    }
}
