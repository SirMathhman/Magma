package magma.app.compile.error;

import magma.api.Ok;
import magma.api.Result;
import magma.app.compile.rule.OrState;

import java.util.List;
import java.util.function.Function;

public record NodeOk<Node, Error, StringResult>(Node node) implements NodeResult<Node, Error, StringResult> {
    @Override
    public NodeResult<Node, Error, StringResult> transform(Function<Node, NodeResult<Node, Error, StringResult>> mapper) {
        return mapper.apply(this.node);
    }

    @Override
    public StringResult generate(Function<Node, StringResult> generator) {
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
