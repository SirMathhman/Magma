package magma.util;

/**
 * Basic associative map interface used throughout the compiler.
 */
public interface Map<K, V> {
    Map<K, V> putAll(Map<K, V> other);

    Iterator<Tuple<K, V>> iter();

    Map<K, V> putTuple(Tuple<K, V> kvTuple);

    Map<K, V> put(K key, V value);

    Option<V> get(K key);
}
