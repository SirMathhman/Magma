// Generated transpiled C++ from 'src\main\java\magma\compile\Node.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Node {int MAX_FORMAT_LEVEL;Map<String, List<Node>> nodeLists;Map<String, Node> nodes;Map<String, String> strings;Option<String> maybeType;};
String escape_Node(String value) {
	return value.replace("", "").replace("", "").replace("", "").replace("", "").replace("", "");
}
String toString_Node() {
	return format(/*???*/);
}
Node withString_Node(String key, String value) {
	strings.put(key, value);
	return this;
}
Option<String> findString_Node(String key) {
	return Option.ofNullable(strings.get(key));
}
Node merge_Node(Node node) {
	maybeType=/*???*/;
	this.strings.putAll(node.strings);
	nodeLists.putAll(node.nodeLists);
	nodes.putAll(node.nodes);
	return this;
}
Node withNodeList_Node(String key, List<Node> values) {
	nodeLists.put(key, values);
	return this;
}
Option<List<Node>> findNodeList_Node(String key) {
	return Option.ofNullable(nodeLists.get(key));
}
Node withNode_Node(String key, Node node) {
	nodes.put(key, node);
	return this;
}
Option<Node> findNode_Node(String key) {
	return Option.ofNullable(nodes.get(key));
}
Node retype_Node(String type) {
	this.maybeType==Option.of(type);
	return this;
}
boolean is_Node(String type) {
	return this.maybeType.map(/*???*/.equals(type)).orElse(false);
}
Set<String> getStringKeys_Node() {
	return strings.keySet();
}
String format_Node(int depth) {
	return format(depth, MAX_FORMAT_LEVEL);
}
String format_Node(int depth, int maxLevel) {
	String indent="".repeat(depth);
	return indent+appendJsonPure(depth, /*???*/, maxLevel);
}
String appendJsonPure_Node(int indentDepth, int level, int maxLevel) {
	String indent="".repeat(indentDepth);
	String childIndent="".repeat(indentDepth+/*???*/);
	StringBuilder builder=new_???();
	builder.append("");
	boolean* hasFields=/*???*/;
	/*???*/=maybeType;
	if (/*???*/)
	{
	builder.append("").append(childIndent).append("").append(escape(value)).append("");
	/*???*/=true;}
	strings.entrySet().stream().sorted(Entry.comparingByKey()).forEach(/*???*/(entry, hasFields, builder, childIndent));
	nodes.entrySet().stream().sorted(Entry.comparingByKey()).forEach(/*???*/(indentDepth, level, maxLevel, entry, hasFields, builder, childIndent));
	nodeLists.entrySet().stream().sorted(Entry.comparingByKey()).forEach(/*???*/(indentDepth, level, maxLevel, entry, hasFields, builder, childIndent));
	if (/*???*/)builder.append("").append(indent);
	builder.append("");
	return builder.toString();
}
void extracted1_Node(int indentDepth, int level, int maxLevel, Entry<String, List<Node>> entry, boolean* hasFields, StringBuilder builder, String childIndent) {
	if (/*???*/)builder.append("");
	else
	builder.append("");
	builder.append(childIndent).append('"').append(escape(entry.getKey())).append("");
	extracted(indentDepth, level, maxLevel, entry, builder, childIndent).append("");
	/*???*/=true;
}
StringBuilder extracted_Node(int indentDepth, int level, int maxLevel, Entry<String, List<Node>> entry, StringBuilder builder, String childIndent) {
	/*???*/();
	return builder;
	if (/*???*/)
	{
	builder.append("");
	return builder;}
	builder.append("");
	builder.append(list.stream().map(/*???*/(indentDepth, level, maxLevel, node)).collect(new_???("")));
	builder.append("").append(childIndent);
	return builder;
}
String getString_Node(int indentDepth, int level, int maxLevel, Node node) {
	String repeat="".repeat(indentDepth+/*???*/);
	String s=node.appendJsonPure(indentDepth+/*???*/, level+/*???*/, maxLevel);
	return repeat+s;
}
void extracted_Node(int indentDepth, int level, int maxLevel, Entry<String, Node> entry, boolean* hasFields, StringBuilder builder, String childIndent) {
	if (/*???*/)builder.append("");
	else
	builder.append("");
	builder.append(childIndent).append('"').append(escape(entry.getKey())).append("");
	if (/*???*/)builder.append(entry.getValue().appendJsonPure(indentDepth+/*???*/, level+/*???*/, maxLevel));
	else
	builder.append("");
	/*???*/=true;
}
void extracted_Node(Entry<String, String> entry, boolean* hasFields, StringBuilder builder, String childIndent) {
	if (/*???*/)builder.append("");
	else
	builder.append("");
	builder.append(childIndent).append('"').append(escape(entry.getKey())).append("").append(escape(entry.getValue())).append('"');
	/*???*/=true;
}
