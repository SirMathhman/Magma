package magma.node;

import java.util.List;
import java.util.Optional;

/**
 * Represents a node in the abstract syntax tree.
 * Nodes can have string properties, lists of child nodes, and a type tag.
 * Nodes are immutable; operations that modify a node return a new node.
 */
public interface Node {
	/**
	 * Adds or replaces a string property of this node.
	 *
	 * @param key   the property key
	 * @param value the string value to store
	 * @return a new node with the added property
	 */
	Node withString(String key, String value);

	/**
	 * Finds a string property of this node.
	 *
	 * @param key the property key
	 * @return an Optional containing the string value, or empty if the property doesn't exist
	 */
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

	/**
	 * Merges this node with another node, combining their properties.
	 * If both nodes have the same property, the property from the other node takes precedence.
	 *
	 * @param other the node to merge with
	 * @return a new node containing properties from both nodes
	 */
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
