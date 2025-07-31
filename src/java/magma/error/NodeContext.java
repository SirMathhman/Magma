package magma.error;

import magma.node.Node;

/**
 * Represents a context with a node value.
 * This can be used to provide additional information about where an error occurred,
 * such as the node that caused the error.
 */
public class NodeContext implements Context {
	private final Node node;

	/**
	 * Creates a new NodeContext with the specified node.
	 *
	 * @param node the node
	 */
	public NodeContext(final Node node) {
		this.node = node;
	}

	/**
	 * Returns the node of this context.
	 *
	 * @return the node
	 */
	public Node getNode() {
		return this.node;
	}

	@Override
	public String display() {
		return this.node.display();
	}
}