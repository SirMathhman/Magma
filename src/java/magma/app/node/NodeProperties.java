package magma.app.node;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NodeProperties<Value> {
    private final Map<String, Value> map;
    private final Completer<Value> completer;

    public NodeProperties(Map<String, Value> map, Completer<Value> completer) {
        this.map = map;
        this.completer = completer;
    }

    public NodeProperties(Completer<Value> completer) {
        this(new HashMap<>(), completer);
    }

    public Node with(String key, Value value) {
        this.map.put(key, value);
        return this.completer.complete(this);
    }

    public Optional<Value> find(String key) {
        if (this.map.containsKey(key))
            return Optional.of(this.map.get(key));

        return Optional.empty();
    }
}