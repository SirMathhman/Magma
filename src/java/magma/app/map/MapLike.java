package magma.app.map;

import magma.app.optional.OptionalLike;

public interface MapLike<Key, Value> {

    OptionalLike<Value> find(Key key);

    MapLike<Key, Value> put(Key key, Value value);
}
