package magma.app.compile;

import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.rule.or.Accumulator;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public record NodeOk<Node extends MergeNode<Node> & TypeNode<Node>, Error>(
        Node node) implements NodeResult<Node, Error> {
    @Override
    public NodeResult<Node, Error> mergeResult(Supplier<NodeResult<Node, Error>> other) {
        return other.get()
                .mergeNode(this.node);
    }

    @Override
    public NodeResult<Node, Error> mergeNode(Node value1) {
        return new NodeOk<>(this.node.merge(value1));
    }

    @Override
    public NodeResult<Node, Error> retype(String type) {
        return new NodeOk<>(this.node.retype(type));
    }

    @Override
    public Accumulator<Node, Error> attachToState(Accumulator<Node, Error> state) {
        return state.withValue(this.node());
    }

    @Override
    public NodeResult<Node, Error> transform(Function<Node, Node> transformer) {
        return new NodeOk<>(transformer.apply(this.node()));
    }

    @Override
    public StringResult<Error> generate(Function<Node, StringResult<Error>> mapper) {
        return mapper.apply(this.node());
    }

    @Override
    public Result<List<Node>, Error> appendTo(List<Node> list) {
        list.add(this.node);
        return new Ok<>(list);
    }
}
