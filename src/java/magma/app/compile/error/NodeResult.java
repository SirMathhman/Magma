package magma.app.compile.error;

import magma.api.Result;
import magma.app.compile.rule.OrState;

import java.util.List;
import java.util.function.Function;

public interface NodeResult<Node> {
    NodeResult<Node> transform(Function<Node, NodeResult<Node>> mapper);

    StringResult generate(Function<Node, StringResult> generator);

    OrState<Node, FormattedError> attachToState(OrState<Node, FormattedError> nodeState);

    Result<List<Node>, FormattedError> attachToList(List<Node> nodes);
}
