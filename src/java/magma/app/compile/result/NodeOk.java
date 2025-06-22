package magma.app.compile.result;

import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.node.Node;

import java.util.function.Function;

public record NodeOk(Node node) implements NodeResult {
    @Override
    public StringResult generate(final Function<Node, StringResult> mapper) {
        return mapper.apply(this.node);
    }

    @Override
    public <Return> Result<Return, CompileError> map(final Function<Node, Return> mapper) {
        return new Ok<>(mapper.apply(this.node));
    }
}


