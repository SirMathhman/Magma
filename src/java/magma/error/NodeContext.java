package magma.error;

import magma.node.Node;

/**
 * Represents a context with a node value.
 * This can be used to provide additional information about where an error occurred,
 * such as the node that caused the error.
 */
public record NodeContext(Node node) implements Context {
	/**
	 * Creates a new NodeContext with the specified node.
	 *
	 * @param node the node
	 */
	public NodeContext {
	}

	@Override
	public String display() {
		return this.node.display();
	}
}