package magma.app.compile.error.node;

import magma.api.Result;
import magma.app.compile.error.CompileError;
import magma.app.compile.error.string.StringResult;
import magma.app.compile.rule.State;

import java.util.List;
import java.util.function.Function;

public interface NodeResult<Node> {
    NodeResult<Node> transform(Function<Node, NodeResult<Node>> mapper);

    StringResult generate(Function<Node, StringResult> generator);

    State<Node> attachToState(State<Node> nodeState);

    Result<List<Node>, CompileError> attachToList(List<Node> nodes);
}
