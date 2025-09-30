package magma.compile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Node {
	private final Map<String, String> strings = new HashMap<>();
	public final Map<String, List<Node>> nodeLists = new HashMap<>();
	public final Map<String, Node> nodes = new HashMap<>();
	public Optional<String> maybeType = Optional.empty();

	private static String escape(String value) {
		return value.replace("\\", "\\\\")
								.replace("\"", "\\\"")
								.replace("\n", "\\n")
								.replace("\r", "\\r")
								.replace("\t", "\\t");
	}

	public Node withString(String key, String value) {
		strings.put(key, value);
		return this;
	}

	public Optional<String> findString(String key) {
		return Optional.ofNullable(strings.get(key));
	}

	public Node merge(Node node) {
		this.strings.putAll(node.strings);
		nodeLists.putAll(node.nodeLists);
		nodes.putAll(node.nodes);
		return this;
	}

	public Node withNodeList(String key, List<Node> values) {
		nodeLists.put(key, values);
		return this;
	}

	public Optional<List<Node>> findNodeList(String key) {
		return Optional.ofNullable(nodeLists.get(key));
	}

	public Node withNode(String key, Node node) {
		nodes.put(key, node);
		return this;
	}

	public Optional<Node> findNode(String key) {
		return Optional.ofNullable(nodes.get(key));
	}

	public Node retype(String type) {
		this.maybeType = Optional.of(type);
		return this;
	}

	public boolean is(String type) {
		return this.maybeType.isPresent() && maybeType.get().equals(type);
	}

	public String format(int depth) {
		StringBuilder builder = new StringBuilder();
		appendJson(builder, depth);
		return builder.toString();
	}

	private void appendJson(StringBuilder builder, int depth) {
		final String indent = "\t".repeat(depth);
		final String childIndent = "\t".repeat(depth + 1);
		builder.append(indent).append("{");
		boolean hasFields = false;

		if (maybeType.isPresent()) {
			builder.append("\n").append(childIndent).append("\"@type\": \"").append(escape(maybeType.get())).append("\"");
			hasFields = true;
		}

		final List<Map.Entry<String, String>> sortedStrings =
				strings.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList();
		for (Map.Entry<String, String> entry : sortedStrings) {
			builder.append(hasFields ? ",\n" : "\n");
			builder.append(childIndent)
						 .append('"')
						 .append(escape(entry.getKey()))
						 .append("\": \"")
						 .append(escape(entry.getValue()))
						 .append("\"");
			hasFields = true;
		}

		final List<Map.Entry<String, Node>> sortedNodes =
				nodes.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList();
		for (Map.Entry<String, Node> entry : sortedNodes) {
			builder.append(hasFields ? ",\n" : "\n");
			builder.append(childIndent).append('"').append(escape(entry.getKey())).append("\": ");
			entry.getValue().appendJson(builder, depth + 1);
			hasFields = true;
		}

		final List<Map.Entry<String, List<Node>>> sortedLists =
				nodeLists.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList();
		for (Map.Entry<String, List<Node>> entry : sortedLists) {
			builder.append(hasFields ? ",\n" : "\n");
			builder.append(childIndent).append('"').append(escape(entry.getKey())).append("\": [");
			List<Node> list = entry.getValue();
			if (!list.isEmpty()) {
				builder.append("\n");
				for (int i = 0; i < list.size(); i++) {
					builder.append("\t".repeat(depth + 2));
					list.get(i).appendJson(builder, depth + 2);
					if (i < list.size() - 1) builder.append(",\n");
					else builder.append("\n");
				}
				builder.append(childIndent);
			}
			builder.append("]");
			hasFields = true;
		}

		if (hasFields) builder.append("\n").append(indent);
		builder.append("}");
	}
}
