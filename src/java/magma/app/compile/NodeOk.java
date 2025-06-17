package magma.app.compile;

import java.util.function.Function;
import java.util.function.Supplier;

public record NodeOk<Node extends MergeNode<Node> & TypeNode<Node>, Error, Iterable>(
        Node node) implements NodeResult<Node, Error, Iterable> {
    @Override
    public NodeResult<Node, Error, Iterable> mergeResult(Supplier<NodeResult<Node, Error, Iterable>> other) {
        return other.get()
                .mergeNode(this.node);
    }

    @Override
    public NodeResult<Node, Error, Iterable> mergeNode(Node value1) {
        return new NodeOk<>(this.node.merge(value1));
    }

    @Override
    public NodeResult<Node, Error, Iterable> retype(String type) {
        return new NodeOk<>(this.node.retype(type));
    }

    @Override
    public Accumulator<Node, Error, Iterable> attachToAccumulator(Accumulator<Node, Error, Iterable> state) {
        return state.withValue(this.node());
    }

    @Override
    public NodeResult<Node, Error, Iterable> transform(Function<Node, Node> transformer) {
        return new NodeOk<>(transformer.apply(this.node()));
    }

    @Override
    public StringResult<Error, Iterable> generate(Function<Node, StringResult<Error, Iterable>> mapper) {
        return mapper.apply(this.node());
    }
}
