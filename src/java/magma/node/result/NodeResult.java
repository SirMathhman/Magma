package magma.node.result;

import magma.error.CompileError;
import magma.node.Node;
import magma.result.Result;

import java.util.function.Function;

public interface NodeResult extends Matching<Node> {
    <Return> Result<Return, CompileError> map(Function<Node, Return> mapper);
}
