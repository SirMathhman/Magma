package magma.node.result;

import magma.error.CompileError;
import magma.node.Node;
import magma.result.Err;
import magma.result.Result;

import java.util.function.Function;

public record NodeErr(CompileError error) implements NodeResult {
    @Override
    public <Return> Return match(final Function<Node, Return> whenOk, final Function<CompileError, Return> whenError) {
        return whenError.apply(this.error);
    }

    @Override
    public <Return> Result<Return, CompileError> map(final Function<Node, Return> mapper) {
        return new Err<>(this.error);
    }
}
