package magma.app.compile.error.node;

import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.node.Node;
import magma.app.compile.rule.or.OrState;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public record NodeOk(Node node) implements NodeResult<Node> {
    @Override
    public NodeResult<Node> mergeResult(Supplier<NodeResult<Node>> other) {
        return other.get()
                .mergeNode(this.node);
    }

    @Override
    public NodeResult<Node> mergeNode(Node value1) {
        return new NodeOk(this.node.merge(value1));
    }

    @Override
    public NodeResult<Node> retype(String type) {
        return new NodeOk(this.node.retype(type));
    }

    @Override
    public OrState<Node, FormattedError> attachToState(OrState<Node, FormattedError> state) {
        return state.withValue(this.node());
    }

    @Override
    public NodeResult<Node> transform(Function<Node, Node> transformer) {
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
