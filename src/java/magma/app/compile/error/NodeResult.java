package magma.app.compile.error;

import magma.app.compile.node.Node;
import magma.app.compile.rule.or.OrState;

public sealed interface NodeResult extends MergeNodeResult<Node, NodeResult>, TypeNodeResult<NodeResult> permits NodeErr, NodeOk {
    OrState<Node, FormattedError> attachToState(OrState<Node, FormattedError> state);
}