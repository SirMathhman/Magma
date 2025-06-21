package magma.api.collect.map;

import magma.api.optional.OptionalLike;
import magma.api.optional.Optionals;

import java.util.HashMap;
import java.util.Map;

public record JavaMap<Key, Value>(Map<Key, Value> map) implements MapLike<Key, Value> {
    public JavaMap() {
        this(new HashMap<>());
    }

    @Override
    public OptionalLike<Value> find(final Key key) {
        if (this.map.containsKey(key)) {
            final var found = this.map.get(key);
            return Optionals.of(found);
        }
        else
            return Optionals.empty();
    }

    @Override
    public MapLike<Key, Value> put(final Key key, final Value value) {
        this.map.put(key, value);
        return this;
    }
}
