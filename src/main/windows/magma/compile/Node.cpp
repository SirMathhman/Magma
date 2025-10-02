// Generated transpiled C++ from 'src\main\java\magma\compile\Node.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Node {};
/*public final Map<String, List<Node>> nodeLists = new*/ HashMap<>_Node() {
}
/*public final Map<String, Node> nodes = new*/ HashMap<>_Node() {
}
/*private final Map<String, String> strings = new*/ HashMap<>_Node() {
}
/*public Option<String> maybeType =*/ Option.empty_Node() {
}
/*private static String*/ escape_Node(/*String*/ value) {
	/*return value.replace*/(/*"\\"*/, /* "\\\\")
								.replace("\""*/, /* "\\\"")
								.replace("\n"*/, /* "\\n")
								.replace("\r"*/, /* "\\r")
								.replace("\t"*/, /* "\\t")*/;
}
/*@Override
	public String*/ toString_Node() {
	/*return format*/(/*0)*/;
}
/*public Node*/ withString_Node(/*String*/ key, /* String*/ value) {
	/*strings.put*/(/*key*/, /* value)*/;
	return /*this*/;
}
/*public Option<String>*/ findString_Node(/*String*/ key) {
	/*return Option.ofNullable*/(/*strings.get(key))*/;
}
/*public Node*/ merge_Node(/*Node*/ node) {
	/*maybeType */=/* switch (maybeType) {
			case None<String> _ -> node.maybeType;
			case Some<String> _ -> maybeType;
		}*/;
	/*this.strings.putAll*/(/*node.strings)*/;
	/*nodeLists.putAll*/(/*node.nodeLists)*/;
	/*nodes.putAll*/(/*node.nodes)*/;
	return /*this*/;
}
/*public Node*/ withNodeList_Node(/*String*/ key, /* List<Node>*/ values) {
	/*nodeLists.put*/(/*key*/, /* values)*/;
	return /*this*/;
}
/*public Option<List<Node>>*/ findNodeList_Node(/*String*/ key) {
	/*return Option.ofNullable*/(/*nodeLists.get(key))*/;
}
/*public Node*/ withNode_Node(/*String*/ key, /* Node*/ node) {
	/*nodes.put*/(/*key*/, /* node)*/;
	return /*this*/;
}
/*public Option<Node>*/ findNode_Node(/*String*/ key) {
	/*return Option.ofNullable*/(/*nodes.get(key))*/;
}
/*public Node*/ retype_Node(/*String*/ type) {
	/*this.maybeType */=/* Option.of(type)*/;
	return /*this*/;
}
/*public boolean*/ is_Node(/*String*/ type) {
	/*return this.maybeType.map*/(/*inner -> inner.equals(type)).orElse(false)*/;
}
/*public Set<String>*/ getStringKeys_Node() {
	/*return strings.keySet*/(/*)*/;
}
/*public String*/ format_Node(/*int*/ depth) {
	/*String indent */=/* "\t".repeat(depth)*/;
	/*String childIndent */=/* "\t".repeat(depth + 1)*/;
	/*String builder */=/* indent + appendJsonPure(depth)*/;
	return /*builder*/;
}
/*private String*/ appendJsonPure_Node(/*int*/ depth) {
	/*final String indent */=/* "\t".repeat(depth)*/;
	/*final String childIndent */=/* "\t".repeat(depth + 1)*/;
	/*StringBuilder builder */=/* new StringBuilder()*/;
	/*builder.append*/(/*"{")*/;
	/*boolean[] hasFields */=/* {false}*/;
	/*Option<String> typeOpt */=/* maybeType*/;
	if (/*typeOpt instanceof Some<String>(String value))*/
	{
	/*builder.append*/(/*"\n").append(childIndent).append("\"@type\": \"").append(escape(value)).append("\"")*/;
	/*hasFields[0] */=/* true*/;}
	/*strings.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
			builder.append(hasFields[0] ? ",\n" : "\n");
			builder.append(childIndent)
						 .append('"')
						 .append(escape(entry.getKey()))
						 .append("\": \"")
						 .append(escape(entry.getValue()))
						 .append('"');
			hasFields[0] */=/* true;
		})*/;
	/*nodes.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
			builder.append(hasFields[0] ? ",\n" : "\n");
			builder.append(childIndent).append('"').append(escape(entry.getKey())).append("\": ");
			builder.append(entry.getValue().appendJsonPure(depth + 1));
			hasFields[0] */=/* true;
		})*/;
	/*nodeLists.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
			builder.append(hasFields[0] ? ",\n" : "\n");
			builder.append(childIndent).append('"').append(escape(entry.getKey())).append("\": [");
			List<Node> list */=/* entry.getValue();
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
	/*if */(/*hasFields[0]) builder.append("\n").append(indent)*/;
	/*builder.append*/(/*"}")*/;
	/*return builder.toString*/(/*)*/;
}
