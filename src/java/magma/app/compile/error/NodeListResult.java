package magma.app.compile.error;

import magma.app.compile.node.Node;

import java.util.function.Supplier;

public sealed interface NodeListResult permits NodeListErr, NodeListOk {
    NodeListResult add(Supplier<NodeResult<Node>> action);
}
