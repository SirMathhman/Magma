package magma.app.compile;

import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.rule.or.Accumulator;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public record NodeOk(Node node) implements NodeResult<Node, FormattedError> {
    @Override
    public NodeResult<Node, FormattedError> mergeResult(Supplier<NodeResult<Node, FormattedError>> other) {
        return other.get()
                .mergeNode(this.node);
    }

    @Override
    public NodeResult<Node, FormattedError> mergeNode(Node value1) {
        return new NodeOk(this.node.merge(value1));
    }

    @Override
    public NodeResult<Node, FormattedError> retype(String type) {
        return new NodeOk(this.node.retype(type));
    }

    @Override
    public Accumulator<Node, FormattedError> attachToState(Accumulator<Node, FormattedError> state) {
        return state.withValue(this.node());
    }

    @Override
    public NodeResult<Node, FormattedError> transform(Function<Node, Node> transformer) {
        return new NodeOk(transformer.apply(this.node()));
    }

    @Override
    public StringResult generate(Function<Node, StringResult> mapper) {
        return mapper.apply(this.node());
    }

    @Override
    public Result<List<Node>, FormattedError> appendTo(List<Node> list) {
        list.add(this.node);
        return new Ok<>(list);
    }
}
