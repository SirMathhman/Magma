package magma.app.compile.error;

import magma.api.Err;
import magma.api.Result;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.OrState;

import java.util.List;
import java.util.function.Function;

public record NodeErr(FormattedError error) implements NodeResult<NodeWithEverything> {
    @Override
    public NodeResult<NodeWithEverything> transform(Function<NodeWithEverything, NodeResult<NodeWithEverything>> mapper) {
        return this;
    }

    @Override
    public StringResult generate(Function<NodeWithEverything, StringResult> generator) {
        return StringResults.Err(this.error);
    }

    @Override
    public OrState<NodeWithEverything, FormattedError> attachToState(OrState<NodeWithEverything, FormattedError> state) {
        return state.withError(this.error);
    }

    @Override
    public Result<List<NodeWithEverything>, FormattedError> attachToList(List<NodeWithEverything> list) {
        return new Err<>(this.error);
    }
}
