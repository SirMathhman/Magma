package magma.app.compile;

import magma.api.result.Err;
import magma.api.result.Result;
import magma.app.compile.rule.or.Accumulator;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public record NodeErr<Node, Error>(Error error) implements NodeResult<Node, Error> {
    @Override
    public NodeResult<Node, Error> mergeResult(Supplier<NodeResult<Node, Error>> other) {
        return new NodeErr<Node, Error>(this.error);
    }

    @Override
    public NodeResult<Node, Error> mergeNode(Node value1) {
        return new NodeErr<Node, Error>(this.error);
    }

    @Override
    public NodeResult<Node, Error> retype(String type) {
        return new NodeErr<Node, Error>(this.error);
    }

    @Override
    public Accumulator<Node, Error> attachToState(Accumulator<Node, Error> state) {
        return state.withError(this.error);
    }

    @Override
    public NodeResult<Node, Error> transform(Function<Node, Node> transformer) {
        return new NodeErr<Node, Error>(this.error);
    }

    @Override
    public StringResult<Error> generate(Function<Node, StringResult<Error>> mapper) {
        return new StringErr<Error>(this.error);
    }

    @Override
    public Result<List<Node>, Error> appendTo(List<Node> list) {
        return new Err<>(this.error);
    }
}