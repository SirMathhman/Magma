package magma.node;

import java.util.Optional;

public interface Node {
	Node withString(String key, String value);

	Optional<String> findString(String key);
	
	Node merge(Node other);
	
	/**
	 * Returns the type tag of this node, if any.
	 * @return an Optional containing the type tag, or empty if no type is set
	 */
	Optional<String> type();
	
	/**
	 * Sets or overrides the type tag of this node.
	 * @param type the new type tag
	 * @return this node (for method chaining)
	 */
	Node retype(String type);
	
	/**
	 * Checks if this node has the specified type tag.
	 * @param type the type tag to check for
	 * @return true if this node has the specified type tag, false otherwise
	 */
	boolean is(String type);
}
