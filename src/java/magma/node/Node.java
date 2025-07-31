package magma.node;

import java.util.List;
import java.util.Optional;

public interface Node {
	Node withString(String key, String value);

	Optional<String> findString(String key);
	
	/**
	 * Adds or replaces a list of nodes as a property of this node.
	 * @param key the property key
	 * @param nodes the list of nodes to store
	 * @return this node (for method chaining)
	 */
	Node withNodeList(String key, List<Node> nodes);
	
	/**
	 * Finds a list of nodes stored as a property of this node.
	 * @param key the property key
	 * @return an Optional containing the list of nodes, or empty if the property doesn't exist
	 */
	Optional<List<Node>> findNodeList(String key);
	
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

	/**
	 * Displays this node in a human-readable format.
	 * This can be used to provide additional information about where an error occurred.
	 *
	 * @return a string representation of this node
	 */
	String display();
}
