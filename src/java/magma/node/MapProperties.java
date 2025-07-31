package magma.node;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * An implementation of Properties that uses a HashMap to store key-value pairs.
 * This class provides an immutable map-like structure, creating a new instance
 * whenever properties are modified.
 *
 * @param <T> the type of values stored in the properties
 */
final class MapProperties<T> implements Properties<T> {
	private final Map<String, T> map;

	/**
	 * Creates a new empty MapProperties instance.
	 */
	MapProperties() {
		this(new HashMap<>());
	}

	/**
	 * Creates a new MapProperties instance with the specified map.
	 * The map is copied and made unmodifiable to ensure immutability.
	 *
	 * @param map the map to initialize with
	 */
	private MapProperties(Map<String, T> map) {
		this.map = Collections.unmodifiableMap(new HashMap<>(map));
	}

	/**
	 * {@inheritDoc}
	 * Creates a new MapProperties instance with the specified key-value pair added.
	 */
	@Override
	public Properties<T> with(final String key, final T value) {
		Map<String, T> newMap = new HashMap<>(this.map); newMap.put(key, value); return new MapProperties<>(newMap);
	}

	/**
	 * {@inheritDoc}
	 * Returns an Optional containing the value associated with the key,
	 * or an empty Optional if the key is not found.
	 */
	@Override
	public Optional<T> find(final String key) {
		return Optional.ofNullable(this.map.get(key));
	}

	/**
	 * {@inheritDoc}
	 * Returns a stream of all entries in the underlying map.
	 */
	@Override
	public Stream<Map.Entry<String, T>> stream() {
		return this.map.entrySet().stream();
	}

	/**
	 * {@inheritDoc}
	 * Checks if the underlying map contains the specified key.
	 */
	@Override
	public boolean has(final String key) {
		return this.map.containsKey(key);
	}
}
