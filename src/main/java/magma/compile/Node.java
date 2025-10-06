package magma.compile;

import magma.list.Joiner;
import magma.list.List;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class Node {
	private static final int MAX_FORMAT_LEVEL = 3;
	public final Map<String, List<Node>> nodeLists = new HashMap<>();
	public final Map<String, Node> nodes = new HashMap<>();
	private final Map<String, String> strings = new HashMap<>();
	public Option<String> maybeType = Option.empty();

	private static String escape(String value) {
		return value.replace("\\", "\\\\")
								.replace("\"", "\\\"")
								.replace("\n", "\\n")
								.replace("\r", "\\r")
								.replace("\t", "\\t");
	}

	@Override
	public String toString() {
		return format(0);
	}

	public Node withString(String key, String value) {
		strings.put(key, value);
		return this;
	}

	public Option<String> findString(String key) {
		return Option.ofNullable(strings.get(key));
	}

	public Node merge(Node node) {
		maybeType = switch (maybeType) {
			case None<String> _ -> node.maybeType;
			case Some<String> _ -> maybeType;
		};
		this.strings.putAll(node.strings);
		nodeLists.putAll(node.nodeLists);
		nodes.putAll(node.nodes);
		return this;
	}

	public Node withNodeList(String key, List<Node> values) {
		nodeLists.put(key, values);
		return this;
	}

	public Option<List<Node>> findNodeList(String key) {
		return Option.ofNullable(nodeLists.get(key));
	}

	public Node withNode(String key, Node node) {
		nodes.put(key, node);
		return this;
	}

	public Option<Node> findNode(String key) {
		return Option.ofNullable(nodes.get(key));
	}

	public Node retype(String type) {
		this.maybeType = Option.of(type);
		return this;
	}

	public boolean is(String type) {
		return this.maybeType.map(inner -> inner.equals(type)).orElse(false);
	}

	public Set<String> getStringKeys() {
		return strings.keySet();
	}

	public String format(int depth) {
		return format(depth, MAX_FORMAT_LEVEL);
	}

	public String format(int depth, int maxLevel) {
		String indent = "\t".repeat(depth);
		return indent + appendJsonPure(depth, 0, maxLevel);
	}

	private String appendJsonPure(int indentDepth, int level, int maxLevel) {
		final String indent = "\t".repeat(indentDepth);
		final String childIndent = "\t".repeat(indentDepth + 1);
		StringBuilder builder = new StringBuilder();
		builder.append("{");

		boolean[] hasFields = new boolean[]{false};

		Option<String> typeOpt = maybeType;
		if (typeOpt instanceof Some<String>(String value)) {
			builder.append("\n").append(childIndent).append("\"@type\": \"").append(escape(value)).append("\"");
			hasFields[0] = true;
		}

		strings.entrySet().stream().sorted(Entry.comparingByKey()).forEach(entry -> {
			extracted(entry, hasFields, builder, childIndent);
		});

		nodes.entrySet().stream().sorted(Entry.comparingByKey()).forEach(entry -> {
			extracted(indentDepth, level, maxLevel, entry, hasFields, builder, childIndent);
		});

		nodeLists.entrySet().stream().sorted(Entry.comparingByKey()).forEach(entry -> {
			extracted1(indentDepth, level, maxLevel, entry, hasFields, builder, childIndent);
		});

		if (hasFields[0]) builder.append("\n").append(indent);
		builder.append("}");
		return builder.toString();
	}

	private void extracted1(int indentDepth,
													int level,
													int maxLevel,
													Entry<String, List<Node>> entry,
													boolean[] hasFields,
													StringBuilder builder,
													String childIndent) {
		if (hasFields[0]) builder.append(",\n");
		else builder.append("\n");
		builder.append(childIndent).append('"').append(escape(entry.getKey())).append("\": [");
		extracted(indentDepth, level, maxLevel, entry, builder, childIndent).append("]");
		hasFields[0] = true;
	}

	private StringBuilder extracted(int indentDepth,
																	int level,
																	int maxLevel,
																	Entry<String, List<Node>> entry,
																	StringBuilder builder,
																	String childIndent) {
		List<Node> list = entry.getValue();
		if (list.isEmpty()) return builder;
		if (level + 1 >= maxLevel) {
			builder.append("...");
			return builder;
		}

		builder.append("\n");
		builder.append(list.stream()
											 .map(node -> getString(indentDepth, level, maxLevel, node))
											 .collect(new Joiner(",\n")));
		builder.append("\n").append(childIndent);
		return builder;
	}

	private String getString(int indentDepth, int level, int maxLevel, Node node) {
		final String repeat = "\t".repeat(indentDepth + 2);
		final String s = node.appendJsonPure(indentDepth + 2, level + 1, maxLevel);
		return repeat + s;
	}

	private void extracted(int indentDepth,
												 int level,
												 int maxLevel,
												 Entry<String, Node> entry,
												 boolean[] hasFields,
												 StringBuilder builder,
												 String childIndent) {
		if (hasFields[0]) builder.append(",\n");
		else builder.append("\n");
		builder.append(childIndent).append('"').append(escape(entry.getKey())).append("\": ");
		if (level + 1 < maxLevel) builder.append(entry.getValue().appendJsonPure(indentDepth + 1, level + 1, maxLevel));
		else builder.append("{...}");
		hasFields[0] = true;
	}

	private void extracted(Entry<String, String> entry, boolean[] hasFields, StringBuilder builder, String childIndent) {
		if (hasFields[0]) builder.append(",\n");
		else builder.append("\n");
		builder.append(childIndent)
					 .append('"')
					 .append(escape(entry.getKey()))
					 .append("\": \"")
					 .append(escape(entry.getValue()))
					 .append('"');
		hasFields[0] = true;
	}
}
