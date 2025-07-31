package magma.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class MapNode implements Node {
	private final MapProperties<String> strings = new MapProperties<>();
	private final MapProperties<List<Node>> nodeLists = new MapProperties<>();
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
		this.strings.with(key, value);
		return this;
	}

	@Override
	public Optional<String> findString(final String key) {
		return this.strings.find(key);
	}

	@Override
	public Node withNodeList(final String key, final List<Node> nodes) {
		this.nodeLists.with(key, new ArrayList<>(nodes));
		return this;
	}

	@Override
	public Optional<List<Node>> findNodeList(final String key) {
		return this.nodeLists.find(key).map(ArrayList::new); // Return a copy to prevent modification
	}

	@Override
	public Node merge(final Node other) {
		final MapNode result = new MapNode();

		// Copy string properties from this node
		this.strings.stream().forEach(entry -> result.withString(entry.getKey(), entry.getValue()));

		// Copy node list properties from this node
		this.nodeLists.stream().forEach(entry -> result.withNodeList(entry.getKey(), entry.getValue()));

		// Copy string properties from the other node
		// For each property we know about in this node, try to find it in the other node
		this.strings.stream()
								.map(Map.Entry::getKey)
								.forEach(key -> other.findString(key).ifPresent(value -> result.withString(key, value)));

		// Copy node list properties from the other node
		// For each node list property we know about in this node, try to find it in the other node
		this.nodeLists.stream()
									.map(Map.Entry::getKey)
									.forEach(key -> other.findNodeList(key).ifPresent(value -> result.withNodeList(key, value)));

		// For MapNode instances, we can directly access and copy all properties
		if (other instanceof final MapNode mapNode) {
			// Copy string properties
			// Only copy if not already present (this node's properties take precedence)
			mapNode.strings.stream()
										 .filter(entry -> !result.strings.has(entry.getKey()))
										 .forEach(entry -> result.withString(entry.getKey(), entry.getValue()));

			// Copy node list properties
			// Only copy if not already present (this node's properties take precedence)
			mapNode.nodeLists.stream()
											 .filter(entry -> !result.nodeLists.has(entry.getKey()))
											 .forEach(entry -> result.withNodeList(entry.getKey(), entry.getValue()));
		}

		// Handle type tag - prefer the other node's type if it has one
		other.type().ifPresentOrElse(result::retype, () -> this.type().ifPresent(result::retype));

		return result;
	}
}