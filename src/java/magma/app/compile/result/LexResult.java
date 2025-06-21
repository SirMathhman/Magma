package magma.app.compile.result;

import magma.app.compile.node.Node;

import java.util.function.Function;

public interface LexResult {
    GenerateResult generate(Function<Node, GenerateResult> mapper);
}
