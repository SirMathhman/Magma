package magma.app.compile.result;

import magma.app.compile.node.Node;

import java.util.function.Function;

public record LexErr(CompileError error) implements LexResult {
    @Override
    public GenerateResult generate(final Function<Node, GenerateResult> mapper) {
        return new GenerateErr(this.error);
    }
}
