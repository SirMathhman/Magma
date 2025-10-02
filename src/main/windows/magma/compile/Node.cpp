// Generated transpiled C++ from 'src\main\java\magma\compile\Node.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Node {new HashMap<>();new HashMap<>();new HashMap<>();/*=*/ Option.empty();};
char* escape_Node(char* value) {
	return /*value.replace("\\", "\\\\")
								.replace("\"", "\\\"")
								.replace("\n", "\\n")
								.replace("\r", "\\r")
								.replace("\t", "\\t")*/;
}
char* toString_Node() {
	return /*format(0)*/;
}
Node withString_Node(char* key, char* value) {
	/*strings.put*/(/*key*/, /* value)*/;
	return /*this*/;
}
Option<String> findString_Node(char* key) {
	return /*Option.ofNullable(strings.get(key))*/;
}
Node merge_Node(Node node) {
	/*maybeType = switch */(/*maybeType) {
			case None<String> _ -> node.maybeType;
			case Some<String> _ -> maybeType;
		}*/;
	/*this.strings.putAll*/(/*node.strings)*/;
	/*nodeLists.putAll*/(/*node.nodeLists)*/;
	/*nodes.putAll*/(/*node.nodes)*/;
	return /*this*/;
}
Node withNodeList_Node(char* key, List<Node> values) {
	/*nodeLists.put*/(/*key*/, /* values)*/;
	return /*this*/;
}
Option<List<Node>> findNodeList_Node(char* key) {
	return /*Option.ofNullable(nodeLists.get(key))*/;
}
Node withNode_Node(char* key, Node node) {
	/*nodes.put*/(/*key*/, /* node)*/;
	return /*this*/;
}
Option<Node> findNode_Node(char* key) {
	return /*Option.ofNullable(nodes.get(key))*/;
}
Node retype_Node(char* type) {
	/*this.maybeType = Option.of*/(/*type)*/;
	return /*this*/;
}
boolean is_Node(char* type) {
	return /*this.maybeType.map(inner -> inner.equals(type)).orElse(false)*/;
}
Set<String> getStringKeys_Node() {
	return /*strings.keySet()*/;
}
char* format_Node(int depth) {
	/*String indent = "\t".repeat*/(/*depth)*/;
	/*String childIndent = "\t".repeat*/(/*depth + 1)*/;
	/*String builder = indent + appendJsonPure*/(/*depth)*/;
	return /*builder*/;
}
char* appendJsonPure_Node(int depth) {
	/*final String indent = "\t".repeat*/(/*depth)*/;
	/*final String childIndent = "\t".repeat*/(/*depth + 1)*/;
	/*StringBuilder builder = new StringBuilder*/(/*)*/;
	/*builder.append*/(/*"{")*/;
	boolean* hasFields=/* {false}*/;
	Option<String> typeOpt=/* maybeType*/;
	if (/*typeOpt instanceof Some<String>(String value))*/)
	{
	/*builder.append*/(/*"\n")).append(childIndent)).append("\"@type\": \"")).append(escape(value))).append("\""));
			hasFields[0] = true*/;}
	/*strings.entrySet*/(/*).stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
			builder.append(hasFields[0] ? ",\n" : "\n");
			builder.append(childIndent)
						 .append('"')
						 .append(escape(entry.getKey()))
						 .append("\": \"")
						 .append(escape(entry.getValue()))
						 .append('"');
			hasFields[0] = true;
		})*/;
	/*nodes.entrySet*/(/*).stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
			builder.append(hasFields[0] ? ",\n" : "\n");
			builder.append(childIndent).append('"').append(escape(entry.getKey())).append("\": ");
			builder.append(entry.getValue().appendJsonPure(depth + 1));
			hasFields[0] = true;
		})*/;
	/*nodeLists.entrySet*/(/*).stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
			builder.append(hasFields[0] ? ",\n" : "\n");
			builder.append(childIndent).append('"').append(escape(entry.getKey())).append("\": [");
			List<Node> list = entry.getValue();
			if (!list.isEmpty()) {
				builder.append("\n");
				builder.append(list.stream()
													 .map(node -> "\t".repeat(depth + 2) + node.appendJsonPure(depth + 2))
													 .collect(Collectors.joining(",\n")));
				builder.append("\n").append(childIndent);
			}
			builder.append("]");
			hasFields[0] = true;
		})*/;
	if (/*hasFields[0])*/)
	/*builder.append*/(/*"\n")).append(indent))*/;
	/*builder.append*/(/*"}")*/;
	return /*builder.toString()*/;
}
