package magma.app.compile.error;

import magma.api.Err;
import magma.api.Result;
import magma.app.compile.rule.OrState;

import java.util.List;
import java.util.function.Function;

public record NodeErr<Node, Error>(Error error) implements NodeResult<Node, Error, StringResult<Error>> {
    @Override
    public NodeResult<Node, Error, StringResult<Error>> transform(Function<Node, NodeResult<Node, Error, StringResult<Error>>> mapper) {
        return this;
    }

    @Override
    public StringResult<Error> generate(Function<Node, StringResult<Error>> generator) {
        return new StringErr<>(this.error);
    }

    @Override
    public OrState<Node, Error> attachToState(OrState<Node, Error> state) {
        return state.withError(this.error);
    }

    @Override
    public Result<List<Node>, Error> attachToList(List<Node> list) {
        return new Err<>(this.error);
    }
}
