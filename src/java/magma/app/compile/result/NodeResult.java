package magma.app.compile.result;

import magma.api.result.Result;
import magma.app.compile.node.Node;

import java.util.function.Function;

public interface NodeResult {
    StringResult generate(Function<Node, StringResult> mapper);

    <Return> Result<Return, CompileError> map(Function<Node, Return> mapper);
}