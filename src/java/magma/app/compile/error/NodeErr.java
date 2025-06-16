package magma.app.compile.error;

import magma.api.Err;
import magma.api.Result;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.OrState;

import java.util.List;
import java.util.function.Function;

public record NodeErr<Error>(Error error) implements NodeResult<NodeWithEverything, Error> {
    @Override
    public NodeResult<NodeWithEverything, Error> transform(Function<NodeWithEverything, NodeResult<NodeWithEverything, Error>> mapper) {
        return this;
    }

    @Override
    public StringResult<Error> generate(Function<NodeWithEverything, StringResult<Error>> generator) {
        return new StringErr<>(this.error);
    }

    @Override
    public OrState<NodeWithEverything, Error> attachToState(OrState<NodeWithEverything, Error> state) {
        return state.withError(this.error);
    }

    @Override
    public Result<List<NodeWithEverything>, Error> attachToList(List<NodeWithEverything> list) {
        return new Err<>(this.error);
    }
}
