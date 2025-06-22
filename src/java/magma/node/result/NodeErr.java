package magma.node.result;

import magma.error.FormattedError;
import magma.result.Err;
import magma.result.Result;

import java.util.function.Function;

public final class NodeErr<Node> implements NodeResult<Node> {
    private final FormattedError error;

    public NodeErr(final FormattedError error) {
        this.error = error;
    }

    @Override
    public <Return> Return match(final Function<Node, Return> whenOk, final Function<FormattedError, Return> whenError) {
        return whenError.apply(this.error);
    }

    @Override
    public <Return> Result<Return, FormattedError> mapToResult(final Function<Node, Return> mapper) {
        return new Err<>(this.error);
    }

    @Override
    public NodeResult<Node> map(final Function<Node, Node> mapper) {
        return new NodeErr<Node>(this.error);
    }
}
