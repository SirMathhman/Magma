package magma.app.compile.error;

import magma.api.Ok;
import magma.api.Result;
import magma.app.compile.rule.OrState;

import java.util.List;
import java.util.function.Function;

public record NodeOk<Node, Error>(Node node) implements NodeResult<Node, Error> {
    @Override
    public NodeResult<Node, Error> transform(Function<Node, NodeResult<Node, Error>> mapper) {
        return mapper.apply(this.node);
    }

    @Override
    public StringResult<Error> generate(Function<Node, StringResult<Error>> generator) {
        return generator.apply(this.node);
    }

    @Override
    public OrState<Node, Error> attachToState(OrState<Node, Error> state) {
        return state.withValue(this.node);
    }

    @Override
    public Result<List<Node>, Error> attachToList(List<Node> list) {
        list.add(this.node);
        return new Ok<>(list);
    }
}
