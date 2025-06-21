package magma.api.collect.map;

import magma.api.Tuple;
import magma.api.TupleImpl;
import magma.api.collect.stream.JavaStream;
import magma.api.collect.stream.StreamLike;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public record JavaMap<Key, Value>(Map<Key, Value> map) implements MapLike<Key, Value> {
    public JavaMap() {
        this(new HashMap<>());
    }

    @Override
    public MapLike<Key, Value> put(final Key key, final Value value) {
        this.map.put(key, value);
        return this;
    }

    @Override
    public StreamLike<Tuple<Key, Value>> stream() {
        return new JavaStream<>(this.map.entrySet()
                .stream()
                .map(entry -> new TupleImpl<>(entry.getKey(), entry.getValue())));
    }

    @Override
    public <Return> Return findOrElse(final Key key, final Function<Value, Return> ifPresent, final Supplier<Return> ifMissing) {
        if (this.map.containsKey(key)) {
            final var found = this.map.get(key);
            return ifPresent.apply(found);
        }
        else
            return ifMissing.get();
    }
}
