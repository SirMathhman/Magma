package magma.node;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface Properties<T> {
	Properties<T> with(String key, T value);

	Optional<T> find(String key);

	Stream<Map.Entry<String, T>> stream();

	boolean has(String key);
}
