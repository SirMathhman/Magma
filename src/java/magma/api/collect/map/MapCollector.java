package magma.api.collect.map;

import magma.api.Tuple;
import magma.api.collect.stream.Collector;

public class MapCollector<Key, Value> implements Collector<Tuple<Key, Value>, MapLike<Key, Value>> {
    @Override
    public MapLike<Key, Value> createInitial() {
        return Maps.empty();
    }

    @Override
    public MapLike<Key, Value> fold(final MapLike<Key, Value> current, final Tuple<Key, Value> entry) {
        return current.put(entry.left(), entry.right());
    }
}
