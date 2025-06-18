package magma.api.map;

import magma.api.Tuple;
import magma.api.TupleImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public record JVMMap<Key, Value>(Map<Key, Value> map) implements MapLike<Key, Value> {
    public JVMMap() {
        this(new HashMap<>());
    }

    @Override
    public MapLike<Key, Value> put(Key key, Value value) {
        this.map.put(key, value);
        return this;
    }

    @Override
    public Stream<Tuple<Key, Value>> stream() {
        return this.map.entrySet()
                .stream()
                .map(entry -> new TupleImpl<>(entry.getKey(), entry.getValue()));
    }

    @Override
    public Optional<Value> find(Key key) {
        if (this.map.containsKey(key))
            return Optional.of(this.map.get(key));
        else
            return Optional.empty();
    }
}
