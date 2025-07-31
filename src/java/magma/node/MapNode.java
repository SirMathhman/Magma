package magma.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class MapNode implements Node {
	private final Map<String, String> properties = new HashMap<>();
	private final Map<String, List<Node>> nodeListProperties = new HashMap<>();
	private String typeTag = null;

	@Override
	public Optional<String> type() {
		return Optional.ofNullable(this.typeTag);
	}

	@Override
	public Node retype(final String type) {
		this.typeTag = type;
		return this;
	}

	@Override
	public boolean is(final String type) {
		return null != type && type.equals(this.typeTag);
	}

	@Override
	public Node withString(final String key, final String value) {
		this.properties.put(key, value);
		return this;
	}

	@Override
	public Optional<String> findString(final String key) {
		return Optional.ofNullable(this.properties.get(key));
	}

	@Override
	public Node withNodeList(final String key, final List<Node> nodes) {
		this.nodeListProperties.put(key, new ArrayList<>(nodes));
		return this;
	}

	@Override
	public Optional<List<Node>> findNodeList(final String key) {
		return Optional.ofNullable(this.nodeListProperties.get(key))
									 .map(ArrayList::new); // Return a copy to prevent modification
	}

	@Override
	public Node merge(final Node other) {
		final MapNode result = new MapNode();

		// Copy string properties from this node
		for (final Map.Entry<String, String> entry : this.properties.entrySet())
			result.withString(entry.getKey(), entry.getValue());

		// Copy node list properties from this node
		for (final Map.Entry<String, List<Node>> entry : this.nodeListProperties.entrySet())
			result.withNodeList(entry.getKey(), entry.getValue());

		// Copy string properties from the other node
		// For each property we know about in this node, try to find it in the other node
		for (final String key : this.properties.keySet())
			other.findString(key).ifPresent(value -> result.withString(key, value));

		// Copy node list properties from the other node
		// For each node list property we know about in this node, try to find it in the other node
		for (final String key : this.nodeListProperties.keySet())
			other.findNodeList(key).ifPresent(value -> result.withNodeList(key, value));

		// For MapNode instances, we can directly access and copy all properties
		if (other instanceof final MapNode mapNode) {
			// Copy string properties
			// Only copy if not already present (this node's properties take precedence)
			for (final Map.Entry<String, String> entry : mapNode.properties.entrySet())
				if (!result.properties.containsKey(entry.getKey())) result.withString(entry.getKey(), entry.getValue());

			// Copy node list properties
			// Only copy if not already present (this node's properties take precedence)
			for (final Map.Entry<String, List<Node>> entry : mapNode.nodeListProperties.entrySet())
				if (!result.nodeListProperties.containsKey(entry.getKey())) {
					result.withNodeList(entry.getKey(), entry.getValue());
				}
		}

		// Handle type tag - prefer the other node's type if it has one
		other.type().ifPresentOrElse(result::retype, () -> this.type().ifPresent(result::retype));

		return result;
	}
}