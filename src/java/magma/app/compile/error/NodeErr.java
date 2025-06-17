package magma.app.compile.error;

import magma.api.result.Err;
import magma.api.result.Result;
import magma.app.compile.node.Node;
import magma.app.compile.rule.or.OrState;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public record NodeErr(FormattedError node) implements NodeResult<Node> {
    @Override
    public NodeResult<Node> mergeResult(Supplier<NodeResult<Node>> other) {
        return new NodeErr(this.node);
    }

    @Override
    public NodeResult<Node> mergeNode(Node value1) {
        return new NodeErr(this.node);
    }

    @Override
    public NodeResult<Node> retype(String type) {
        return new NodeErr(this.node);
    }

    @Override
    public OrState<Node, FormattedError> attachToState(OrState<Node, FormattedError> state) {
        return state.withError(this.node());
    }

    @Override
    public NodeResult<Node> transform(Function<Node, Node> transformer) {
        return new NodeErr(this.node());
    }

    @Override
    public StringResult generate(Function<Node, StringResult> mapper) {
        return new StringErr(this.node());
    }

    @Override
    public Result<List<Node>, FormattedError> appendTo(List<Node> list) {
        return new Err<>(this.node());
    }
}
