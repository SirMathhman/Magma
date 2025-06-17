package magma.app.compile.error;

import magma.app.compile.node.Node;

public sealed interface NodeResult extends MergeNodeResult<Node, NodeResult> permits NodeErr, NodeOk {
}
