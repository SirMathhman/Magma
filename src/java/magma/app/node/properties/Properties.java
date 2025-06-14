package magma.app.node.properties;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface Properties<S, V> {
    S with(String key, V value);

    Optional<V> find(String key);

    Stream<Map.Entry<String, V>> stream();

    Properties<S, V> merge(Properties<S, V> other);

    Properties<S, V> add(String key, V value);
}
