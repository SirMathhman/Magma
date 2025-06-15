package magma.app.node.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MapNodeProperties<Value, Node> implements NodeProperties<Value, Node> {
    private final Map<String, Value> map;
    private final Completer<Value, Node> completer;

    public MapNodeProperties(Map<String, Value> map, Completer<Value, Node> completer) {
        this.map = map;
        this.completer = completer;
    }

    public MapNodeProperties(Completer<Value, Node> completer) {
        this(new HashMap<>(), completer);
    }

    @Override
    public Node with(String key, Value value) {
        this.map.put(key, value);
        return this.completer.complete(this);
    }

    @Override
    public Optional<Value> find(String key) {
        if (this.map.containsKey(key))
            return Optional.of(this.map.get(key));

        return Optional.empty();
    }
}