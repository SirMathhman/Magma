package magma.app.compile;

import java.util.function.Supplier;

public interface MergeNodeResult<Node, NodeResult> {
    NodeResult mergeResult(Supplier<NodeResult> other);

    NodeResult mergeNode(Node value1);
}