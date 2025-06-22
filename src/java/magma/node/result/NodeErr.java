package magma.node.result;

import magma.error.CompileError;
import magma.node.Node;
import magma.string.StringErr;
import magma.string.StringResult;

import java.util.function.Function;

public record NodeErr(CompileError error) implements NodeResult {
    @Override
    public StringResult generate(final Function<Node, StringResult> mapper) {
        return new StringErr(this.error);
    }
}
