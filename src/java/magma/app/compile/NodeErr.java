package magma.app.compile;

import magma.api.list.Streamable;

import java.util.function.Function;
import java.util.function.Supplier;

public record NodeErr<Node, Error>(Error error) implements NodeResult<Node, Error> {
    @Override
    public NodeResult<Node, Error> mergeResult(Supplier<NodeResult<Node, Error>> other) {
        return new NodeErr<>(this.error);
    }

    @Override
    public NodeResult<Node, Error> mergeNode(Node value1) {
        return new NodeErr<>(this.error);
    }

    @Override
    public NodeResult<Node, Error> retype(String type) {
        return new NodeErr<>(this.error);
    }

    @Override
    public Accumulator<Node, Error, Streamable<Error>> attachToState(Accumulator<Node, Error, Streamable<Error>> state) {
        return state.withError(this.error);
    }

    @Override
    public NodeResult<Node, Error> transform(Function<Node, Node> transformer) {
        return new NodeErr<>(this.error);
    }

    @Override
    public StringResult<Error> generate(Function<Node, StringResult<Error>> mapper) {
        return new StringErr<>(this.error);
    }
}