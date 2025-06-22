package magma.app.compile.result;

import magma.api.result.Err;
import magma.api.result.Result;
import magma.app.compile.node.Node;

import java.util.function.Function;

public record NodeErr(CompileError error) implements NodeResult {
    @Override
    public StringResult generate(final Function<Node, StringResult> mapper) {
        return new StringErr(this.error);
    }

    @Override
    public <Return> Result<Return, CompileError> map(final Function<Node, Return> mapper) {
        return new Err<>(this.error);
    }
}
