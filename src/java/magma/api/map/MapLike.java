package magma.api.map;

import magma.api.Tuple;

import java.util.stream.Stream;

public interface MapLike<Key, Value> {
    MapLike<Key, Value> put(Key key, Value value);

    Stream<Tuple<Key, Value>> stream();

    boolean containsKey(Key key);

    Value get(Key key);
}
