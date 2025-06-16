package magma.app.compile.error;

import java.util.function.Supplier;

public interface NodeListResult<Node> {
    NodeResult<Node> toNode(String key);

    NodeListResult<Node> add(Supplier<NodeResult<Node>> other);
}
