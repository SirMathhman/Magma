package magma.api.map;

import magma.api.Tuple;

import java.util.Optional;
import java.util.stream.Stream;

public interface MapLike<Key, Value> {
    Optional<Value> find(Key key);

    MapLike<Key, Value> put(Key key, Value value);

    Stream<Tuple<Key, Value>> stream();
}
