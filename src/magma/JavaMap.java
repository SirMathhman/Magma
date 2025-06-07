package magma;

import java.util.ArrayList;
import java.util.HashMap;

record JavaMap<K, V>(java.util.Map<K, V> map) implements Map<K, V> {
    public JavaMap() {
        this(new HashMap<>());
    }

    @Override
    public Map<K, V> putAll(Map<K, V> other) {
        return other.iter().<Map<K, V>>fold(this, Map::putTuple);
    }

    @Override
    public Iterator<Tuple<K, V>> iter() {
        return new JavaList<>(new ArrayList<>(map.entrySet()))
                .iter()
                .map(entry -> new Tuple<>(entry.getKey(), entry.getValue()));
    }

    @Override
    public Map<K, V> putTuple(Tuple<K, V> tuple) {
        map.put(tuple.left, tuple.right);
        return this;
    }

    @Override
    public Map<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    @Override
    public Option<V> get(K key) {
        if (map.containsKey(key)) {
            return new Some<>(map.get(key));
        }
        return new None<>();
    }
}
