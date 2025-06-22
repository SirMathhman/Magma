package magma.node.result;

import magma.error.CompileError;
import magma.node.Node;
import magma.result.Ok;
import magma.result.Result;

import java.util.function.Function;

public record NodeOk(Node node) implements NodeResult {
    @Override
    public <Return> Return match(final Function<Node, Return> whenOk, final Function<CompileError, Return> whenError) {
        return whenOk.apply(this.node);
    }

    @Override
    public <Return> Result<Return, CompileError> map(final Function<Node, Return> mapper) {
        return new Ok<>(mapper.apply(this.node));
    }
}
