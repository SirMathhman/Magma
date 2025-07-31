package magma.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class MapNode implements Node {
	private final Properties<String> strings;
	private final Properties<List<Node>> nodeLists;
	private final Optional<String> typeTag;

	public MapNode() {
		this(new MapProperties<>(), new MapProperties<>(), Optional.empty());
	}

	private MapNode(Properties<String> strings, Properties<List<Node>> nodeLists, Optional<String> typeTag) {
		this.strings = strings; this.nodeLists = nodeLists; this.typeTag = typeTag;
	}

	@Override
	public Optional<String> type() {
		return this.typeTag;
	}

	@Override
	public Node retype(final String type) {
		return new MapNode(this.strings, this.nodeLists, Optional.ofNullable(type));
	}

	@Override
	public boolean is(final String type) {
		return null != type && this.typeTag.isPresent() && type.contentEquals(this.typeTag.get());
	}

	@Override
	public Node withString(final String key, final String value) {
		return new MapNode((Properties<String>) this.strings.with(key, value), this.nodeLists, this.typeTag);
	}

	@Override
	public Optional<String> findString(final String key) {
		return this.strings.find(key);
	}

	@Override
	public Node withNodeList(final String key, final List<Node> nodes) {
		return new MapNode(this.strings, (Properties<List<Node>>) this.nodeLists.with(key, new ArrayList<>(nodes)),
											 this.typeTag);
	}

	@Override
	public Optional<List<Node>> findNodeList(final String key) {
		return this.nodeLists.find(key).map(ArrayList::new); // Return a copy to prevent modification
	}

	@Override
	public Node merge(final Node other) {
		MapNode result = new MapNode();

		// Copy string properties from this node
		for (Map.Entry<String, String> entry : this.strings.stream().toList()) {
			result = (MapNode) result.withString(entry.getKey(), entry.getValue());
		}

		// Copy node list properties from this node
		for (Map.Entry<String, List<Node>> entry : this.nodeLists.stream().toList()) {
			result = (MapNode) result.withNodeList(entry.getKey(), entry.getValue());
		}

		// Copy string properties from the other node, overwriting existing ones
		for (String key : this.strings.stream().map(Map.Entry::getKey).toList()) {
			Optional<String> otherValue = other.findString(key); if (otherValue.isPresent()) {
				result = (MapNode) result.withString(key, otherValue.get());
			}
		}

		// Copy node list properties from the other node, overwriting existing ones
		for (String key : this.nodeLists.stream().map(Map.Entry::getKey).toList()) {
			Optional<List<Node>> otherValue = other.findNodeList(key); if (otherValue.isPresent()) {
				result = (MapNode) result.withNodeList(key, otherValue.get());
			}
		}

		// Handle type tag - prefer this node's type over the other node's type
		if (this.type().isPresent()) {
			result = (MapNode) result.retype(this.type().get());
		} else {
			Optional<String> otherType = other.type(); if (otherType.isPresent()) {
				result = (MapNode) result.retype(otherType.get());
			}
		}
		
		return result;
	}

	@Override
	public String display() {
		final StringBuilder sb = new StringBuilder();

		// Add the type tag if available, or empty string if not
		sb.append(this.typeTag.orElse(""));

		// Get string properties and sort by key
		final List<Map.Entry<String, String>> stringEntries =
				this.strings.stream().sorted(Map.Entry.comparingByKey()).toList();

		// Get node list properties and sort by key
		final List<Map.Entry<String, List<Node>>> nodeListEntries =
				this.nodeLists.stream().sorted(Map.Entry.comparingByKey()).toList();

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
			if (!stringEntries.isEmpty() && !nodeListEntries.isEmpty()) sb.append(", ");

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