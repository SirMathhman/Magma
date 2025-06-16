package magma.app.compile.error;

import magma.api.Ok;
import magma.api.Result;
import magma.app.compile.node.NodeWithEverything;
import magma.app.compile.rule.OrState;

import java.util.List;
import java.util.function.Function;

public record NodeOk<Error>(NodeWithEverything node) implements NodeResult<NodeWithEverything, Error> {
    @Override
    public NodeResult<NodeWithEverything, Error> transform(Function<NodeWithEverything, NodeResult<NodeWithEverything, Error>> mapper) {
        return mapper.apply(this.node);
    }

    @Override
    public StringResult<Error> generate(Function<NodeWithEverything, StringResult<Error>> generator) {
        return generator.apply(this.node);
    }

    @Override
    public OrState<NodeWithEverything, Error> attachToState(OrState<NodeWithEverything, Error> state) {
        return state.withValue(this.node);
    }

    @Override
    public Result<List<NodeWithEverything>, Error> attachToList(List<NodeWithEverything> list) {
        list.add(this.node);
        return new Ok<>(list);
    }
}
