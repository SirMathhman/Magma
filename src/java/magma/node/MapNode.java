package magma.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An immutable implementation of the Node interface that uses map-based
 * structures to store string properties and node list properties.
 * <p>
 * This class represents a node in the abstract syntax tree with a type tag,
 * string properties, and collections of child nodes. All operations that
 * modify the node return a new instance, preserving immutability.
 * <p>
 * MapNode provides methods for accessing and modifying properties, as well as
 * merging nodes and displaying node content in a human-readable format.
 */
public final class MapNode implements Node {
	private final Properties<String> strings;
	private final Properties<List<Node>> nodeLists;
	private final Optional<String> typeTag;

	public MapNode() {
		this(new MapProperties<>(), new MapProperties<>(), Optional.empty());
	}

	private MapNode(final Properties<String> strings,
									final Properties<List<Node>> nodeLists,
									final Optional<String> typeTag) {
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
		return new MapNode(this.strings.with(key, value), this.nodeLists, this.typeTag);
	}

	@Override
	public Optional<String> findString(final String key) {
		return this.strings.find(key);
	}

	@Override
	public Node withNodeList(final String key, final List<Node> nodes) {
		return new MapNode(this.strings, this.nodeLists.with(key, new ArrayList<>(nodes)), this.typeTag);
	}

	@Override
	public Optional<List<Node>> findNodeList(final String key) {
		return this.nodeLists.find(key).map(ArrayList::new); // Return a copy to prevent modification
	}

	@Override
	public Node merge(final Node other) {
		Node result = new MapNode();

		// Copy string properties from this node
		result = this.strings.stream()
												 .reduce(result, (node, entry) -> node.withString(entry.getKey(), entry.getValue()),
																 (_, next) -> next);

		// Copy node list properties from this node
		result = this.nodeLists.stream()
													 .reduce(result, (node, entry) -> node.withNodeList(entry.getKey(), entry.getValue()),
																	 (_, next) -> next);

		// Copy string properties from the other node, overwriting existing ones
		result = this.strings.stream()
												 .map(Map.Entry::getKey)
												 .filter(key -> other.findString(key).isPresent())
												 .reduce(result, (node, key) -> node.withString(key, other.findString(key).get()),
																 (_, next) -> next);

		// Copy node list properties from the other node, overwriting existing ones
		result = this.nodeLists.stream()
													 .map(Map.Entry::getKey)
													 .filter(key -> other.findNodeList(key).isPresent())
													 .reduce(result, (node, key) -> node.withNodeList(key, other.findNodeList(key).get()),
																	 (_, next) -> next);

		// Handle type tag - prefer this node's type over the other node's type
		if (this.type().isPresent()) {result = result.retype(this.type().get());} else {
			final Optional<String> otherType = other.type();
			if (otherType.isPresent()) result = result.retype(otherType.get());
		}

		return result;
	}

	@Override
	public String display() {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.typeTag.orElse(""));

		final Stream<String> stringProps = this.strings.stream()
																									 .sorted(Map.Entry.comparingByKey())
																									 .map(entry -> entry.getKey() + ": \"" + entry.getValue() + "\"");

		final Stream<String> nodeListProps = this.nodeLists.stream()
																											 .sorted(Map.Entry.comparingByKey())
																											 .map(entry -> entry.getKey() + ": [" + entry.getValue().size() +
																																		 " nodes]");

		final String properties = Stream.concat(stringProps, nodeListProps).collect(Collectors.joining(", ", " { ", " }"));

		return sb.append(properties).toString();
	}
}