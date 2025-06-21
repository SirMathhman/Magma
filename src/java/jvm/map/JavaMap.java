package jvm.map;

import magma.app.map.MapLike;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record JavaMap<Key, Value>(Map<Key, Value> map) implements MapLike<Key, Value> {
    public JavaMap() {
        this(new HashMap<>());
    }

    @Override
    public Optional<Value> find(final Key key) {
        if (this.map.containsKey(key)) {
            final var found = this.map.get(key);
            return Optional.of(found);
        }
        else
            return Optional.empty();
    }

    @Override
    public MapLike<Key, Value> put(final Key key, final Value value) {
        this.map.put(key, value);
        return this;
    }
}
