package magma.app.compile.error.node;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface NodeResult<Node> {
    @Deprecated
    Optional<Node> findValue();

    NodeResult<Node> mergeResult(Supplier<NodeResult<Node>> other, BiFunction<Node, Node, Node> merger);

    NodeResult<Node> mergeNode(Node node, BiFunction<Node, Node, Node> merger);
}
