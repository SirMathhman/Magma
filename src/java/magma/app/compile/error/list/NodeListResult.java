package magma.app.compile.error.list;

import magma.app.compile.error.node.NodeResult;
import magma.app.compile.node.Node;

import java.util.function.Supplier;

public sealed interface NodeListResult permits NodeListErr, NodeListOk {
    NodeListResult add(Supplier<NodeResult<Node>> action);
}
