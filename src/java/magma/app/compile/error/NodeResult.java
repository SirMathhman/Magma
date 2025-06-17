package magma.app.compile.error;

import magma.api.result.Result;
import magma.app.compile.rule.or.OrState;

import java.util.List;
import java.util.function.Function;

public interface NodeResult<Node> extends MergeNodeResult<Node, NodeResult<Node>>, TypeNodeResult<NodeResult<Node>> {
    Result<List<Node>, FormattedError> appendTo(List<Node> list);

    StringResult generate(Function<Node, StringResult> mapper);

    NodeResult<Node> transform(Function<Node, Node> transformer);

    OrState<Node, FormattedError> attachToState(OrState<Node, FormattedError> state);
}