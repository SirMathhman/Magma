package magma.app.map;

import java.util.Optional;

public interface MapLike<Key, Value> {

    Optional<Value> find(Key key);

    MapLike<Key, Value> put(Key key, Value value);
}
