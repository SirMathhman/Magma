package magma.node;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents a collection of key-value properties.
 * This interface provides an immutable map-like structure for storing and retrieving properties.
 *
 * @param <T> the type of values stored in the properties
 */
public interface Properties<T> {
	/**
	 * Creates a new Properties instance with the specified key-value pair added.
	 * If the key already exists, its value will be replaced.
	 *
	 * @param key   the property key
	 * @param value the property value
	 * @return a new Properties instance with the added key-value pair
	 */
	Properties<T> with(String key, T value);

	/**
	 * Finds a value by its key.
	 *
	 * @param key the property key to look up
	 * @return an Optional containing the value if found, or empty if not found
	 */
	Optional<T> find(String key);

	/**
	 * Returns a stream of all key-value entries in the properties.
	 *
	 * @return a stream of Map.Entry objects representing all properties
	 */
	Stream<Map.Entry<String, T>> stream();

	/**
	 * Checks if a property with the specified key exists.
	 *
	 * @param key the property key to check
	 * @return true if the property exists, false otherwise
	 */
	boolean has(String key);
}
