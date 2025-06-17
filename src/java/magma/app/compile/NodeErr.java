package magma.app.compile;

import magma.api.result.Err;
import magma.api.result.Result;
import magma.app.compile.rule.or.Accumulator;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public record NodeErr(FormattedError error) implements NodeResult<Node, FormattedError> {
    @Override
    public NodeResult<Node, FormattedError> mergeResult(Supplier<NodeResult<Node, FormattedError>> other) {
        return new NodeErr(this.error);
    }

    @Override
    public NodeResult<Node, FormattedError> mergeNode(Node value1) {
        return new NodeErr(this.error);
    }

    @Override
    public NodeResult<Node, FormattedError> retype(String type) {
        return new NodeErr(this.error);
    }

    @Override
    public Accumulator<Node, FormattedError> attachToState(Accumulator<Node, FormattedError> state) {
        return state.withError(this.error);
    }

    @Override
    public NodeResult<Node, FormattedError> transform(Function<Node, Node> transformer) {
        return new NodeErr(this.error);
    }

    @Override
    public StringResult<FormattedError> generate(Function<Node, StringResult<FormattedError>> mapper) {
        return new StringErr(this.error);
    }

    @Override
    public Result<List<Node>, FormattedError> appendTo(List<Node> list) {
        return new Err<>(this.error);
    }
}