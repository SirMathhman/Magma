package magma.util;

public class MapCollector<K, V> implements Collector<Tuple<K, V>, Map<K, V>> {
    @Override
    public Map<K, V> createInitial() {
        return Maps.empty();
    }

    @Override
    public Map<K, V> fold(Map<K, V> current, Tuple<K, V> element) {
        return current.putTuple(element);
    }
}
