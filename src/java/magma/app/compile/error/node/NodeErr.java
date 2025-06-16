package magma.app.compile.error.node;

import magma.api.Err;
import magma.api.Error;
import magma.api.Result;
import magma.app.compile.error.string.StringErr;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.OrState;

import java.util.List;
import java.util.function.Function;

public record NodeErr(Error error) implements NodeResult<NodeWithEverything> {
    @Override
    public NodeResult<NodeWithEverything> transform(Function<NodeWithEverything, NodeResult<NodeWithEverything>> mapper) {
        return this;
    }

    @Override
    public StringResult generate(Function<NodeWithEverything, StringResult> generator) {
        return new StringErr(this.error);
    }

    @Override
    public OrState<NodeWithEverything> attachToState(OrState<NodeWithEverything> state) {
        return state.withError(this.error);
    }

    @Override
    public Result<List<NodeWithEverything>, Error> attachToList(List<NodeWithEverything> list) {
        return new Err<>(this.error);
    }
}
