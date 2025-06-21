package magma.api.collect.map;

import magma.api.Tuple;
import magma.api.collect.stream.StreamLike;

import java.util.function.Function;
import java.util.function.Supplier;

public interface MapLike<Key, Value> {
    <Return> Return findOrElse(Key key, Function<Value, Return> ifPresent, Supplier<Return> ifMissing);

    MapLike<Key, Value> put(Key key, Value value);

    StreamLike<Tuple<Key, Value>> stream();
}
