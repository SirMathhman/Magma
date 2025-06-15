package magma.app.compile.node.properties;

import java.util.Optional;

public interface NodeProperties<Value, Node> {
    Node with(String key, Value value);

    Optional<Value> find(String key);
}
