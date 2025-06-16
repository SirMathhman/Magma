package magma.app.compile.error;

import java.util.function.Supplier;

public interface NodeListResult<Node, Error> {
    NodeResult<Node, Error> toNode(String key);

    NodeListResult<Node, Error> add(Supplier<NodeResult<Node, Error>> other);
}
