package magma.app.compile.error.node;

import magma.api.result.Err;
import magma.api.result.Result;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.string.StringErr;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.node.Node;
import magma.app.compile.rule.or.Accumulator;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public record NodeErr(FormattedError error) implements NodeResult<Node> {
    @Override
    public NodeResult<Node> mergeResult(Supplier<NodeResult<Node>> other) {
        return new NodeErr(this.error);
    }

    @Override
    public NodeResult<Node> mergeNode(Node value1) {
        return new NodeErr(this.error);
    }

    @Override
    public NodeResult<Node> retype(String type) {
        return new NodeErr(this.error);
    }

    @Override
    public Accumulator<Node, FormattedError> attachToState(Accumulator<Node, FormattedError> state) {
        return state.withError(this.error);
    }

    @Override
    public NodeResult<Node> transform(Function<Node, Node> transformer) {
        return new NodeErr(this.error);
    }

    @Override
    public StringResult generate(Function<Node, StringResult> mapper) {
        return new StringErr(this.error);
    }

    @Override
    public Result<List<Node>, FormattedError> appendTo(List<Node> list) {
        return new Err<>(this.error);
    }
}