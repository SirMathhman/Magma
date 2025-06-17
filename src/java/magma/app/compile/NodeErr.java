package magma.app.compile;

import magma.api.collect.iter.Iterable;

import java.util.function.Function;
import java.util.function.Supplier;

public record NodeErr<Node, Error>(Error error) implements NodeResult<Node, Error, Iterable<Error>> {
    @Override
    public NodeResult<Node, Error, Iterable<Error>> mergeResult(Supplier<NodeResult<Node, Error, Iterable<Error>>> other) {
        return new NodeErr<>(this.error);
    }

    @Override
    public NodeResult<Node, Error, Iterable<Error>> mergeNode(Node value1) {
        return new NodeErr<>(this.error);
    }

    @Override
    public NodeResult<Node, Error, Iterable<Error>> retype(String type) {
        return new NodeErr<>(this.error);
    }

    @Override
    public Accumulator<Node, Error, Iterable<Error>> attachToAccumulator(Accumulator<Node, Error, Iterable<Error>> state) {
        return state.withError(this.error);
    }

    @Override
    public NodeResult<Node, Error, Iterable<Error>> transform(Function<Node, Node> transformer) {
        return new NodeErr<>(this.error);
    }

    @Override
    public StringResult<Error, Iterable<Error>> generate(Function<Node, StringResult<Error, Iterable<Error>>> mapper) {
        return new StringErr<>(this.error);
    }
}