package magma.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class MapNode implements Node {
	private final MapProperties<String> strings = new MapProperties<>();
	private final MapProperties<List<Node>> nodeLists = new MapProperties<>();
	private Optional<String> typeTag = Optional.empty();

	@Override
	public Optional<String> type() {
		return this.typeTag;
	}

	@Override
	public Node retype(final String type) {
		this.typeTag = Optional.ofNullable(type);
		return this;
	}

	@Override
	public boolean is(final String type) {
		return null != type && this.typeTag.isPresent() && type.contentEquals(this.typeTag.get());
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

		// Copy string properties from the other node, overwriting existing ones
		this.strings.stream()
								.map(Map.Entry::getKey)
								.forEach(key -> other.findString(key).ifPresent(value -> result.withString(key, value)));

		// Copy node list properties from the other node, overwriting existing ones
		this.nodeLists.stream()
									.map(Map.Entry::getKey)
									.forEach(key -> other.findNodeList(key).ifPresent(value -> result.withNodeList(key, value)));

		// Handle type tag - prefer this node's type over the other node's type
		this.type().ifPresentOrElse(result::retype, () -> other.type().ifPresent(result::retype));
		return result;
	}

	@Override
	public String display() {
		final StringBuilder sb = new StringBuilder();

		// Add the type tag if available, or "Node" if not
		sb.append(this.typeTag.orElse("Node"));

		// Get string properties
		final List<Map.Entry<String, String>> stringEntries = this.strings.stream().toList();

		// Get node list properties
		final List<Map.Entry<String, List<Node>>> nodeListEntries = this.nodeLists.stream().toList();

		// Add properties in JSON-like format
		if (!stringEntries.isEmpty() || !nodeListEntries.isEmpty()) {
			sb.append(" { ");

			// Add string properties
			if (!stringEntries.isEmpty()) {
				sb.append(stringEntries.stream()
															 .map(entry -> entry.getKey() + ": \"" + entry.getValue() + "\"")
															 .collect(Collectors.joining(", ")));
			}

			// Add separator if both types of properties exist
			if (!stringEntries.isEmpty() && !nodeListEntries.isEmpty()) {
				sb.append(", ");
			}

			// Add node list properties
			if (!nodeListEntries.isEmpty()) {
				sb.append(nodeListEntries.stream()
																 .map(entry -> entry.getKey() + ": [" + entry.getValue().size() + " nodes]")
																 .collect(Collectors.joining(", ")));
			}

			sb.append(" }");
		}

		return sb.toString();
	}
}