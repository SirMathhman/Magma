struct Node{};
char* toString_Node() {/*
		return format(0);
	*/}
char* escape_Node(char* value) {/*
		return value.replace("\\", "\\\\")
								.replace("\"", "\\\"")
								.replace("\n", "\\n")
								.replace("\r", "\\r")
								.replace("\t", "\\t");
	*/}
Node withString_Node(char* key, char* value) {/*
		strings.put(key, value);
		return this;
	*/}
Option<String> findString_Node(char* key) {/*
		return Option.ofNullable(strings.get(key));
	*/}
Node merge_Node(Node node) {/*
		maybeType = switch (maybeType) {
			case None<String> _ -> node.maybeType;
			case Some<String> _ -> maybeType;
		};
		this.strings.putAll(node.strings);
		nodeLists.putAll(node.nodeLists);
		nodes.putAll(node.nodes);
		return this;
	*/}
Node withNodeList_Node(char* key, ListNode values) {/*
		nodeLists.put(key, values);
		return this;
	*/}
Option<ListNode> findNodeList_Node(char* key) {/*
		return Option.ofNullable(nodeLists.get(key));
	*/}
Node withNode_Node(char* key, Node node) {/*
		nodes.put(key, node);
		return this;
	*/}
Option<Node> findNode_Node(char* key) {/*
		return Option.ofNullable(nodes.get(key));
	*/}
Node retype_Node(char* type) {/*
		this.maybeType = Option.of(type);
		return this;
	*/}
boolean is_Node(char* type) {/*
		return this.maybeType.map(inner -> inner.equals(type)).orElse(false);
	*/}
char* format_Node(int depth) {/*
		StringBuilder builder = new StringBuilder();
		builder.append("\t".repeat(depth));
		appendJson(builder, depth);
		return builder.toString();
	*/}
void appendJson_Node(StringBuilder builder, int depth) {/*
		final String indent = "\t".repeat(depth);
		final String childIndent = "\t".repeat(depth + 1);
		builder.append("{");
		boolean hasFields = false;

		if (maybeType instanceof Some<String>(String value)) {
			builder.append("\n").append(childIndent).append("\"@type\": \"").append(escape(value)).append("\"");
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
	*/}
