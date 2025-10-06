// Generated transpiled C++ from 'src\main\java\magma\compile\Node.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Node {};
new HashMap<>_Node() {
}
new HashMap<>_Node() {
}
new HashMap<>_Node() {
}
char* escape_Node(char* value) {
	return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
}
char* toString_Node() {
	return format(0);
}
Node withString_Node(char* key, char* value) {
	strings.put((key, value);
	return this;
}
public Option<> findString_Node(char* key) {
	return Option.ofNullable(strings.get(key));
}
Node merge_Node(Node node) {
	maybeType=???;
	this.strings.putAll((node.strings);
	nodeLists.putAll((node.nodeLists);
	nodes.putAll((node.nodes);
	return this;
}
Node withNodeList_Node(char* key, List<> values) {
	nodeLists.put((key, values);
	return this;
}
public Option<> findNodeList_Node(char* key) {
	return Option.ofNullable(nodeLists.get(key));
}
Node withNode_Node(char* key, Node node) {
	nodes.put((key, node);
	return this;
}
public Option<> findNode_Node(char* key) {
	return Option.ofNullable(nodes.get(key));
}
Node retype_Node(char* type) {
	this.maybeType=Option.of(type);
	return this;
}
boolean is_Node(char* type) {
	return this.maybeType.map(inner -> inner.equals(type)).orElse(false);
}
public Set<> getStringKeys_Node() {
	return strings.keySet();
}
char* format_Node(int depth) {
	return format((depth, MAX_FORMAT_LEVEL);
}
char* format_Node(int depth, int maxLevel) {
	char* indent="".repeat(depth);
	return indent+appendJsonPure((depth, 0, maxLevel);
}
char* appendJsonPure_Node(int indentDepth, int level, int maxLevel) {
	char* indent="".repeat(indentDepth);
	char* childIndent="".repeat(indentDepth + 1);
	new StringBuilder();
	builder.append(("");
	new boolean[]{false};
	Option<> typeOpt=maybeType;
	if (/*???*/)
	{
	builder.append(("\n").append((childIndent).append(("\"@type\": \"").append((escape(value)).append(("");
	/*???*/=true;}
	strings.entrySet(().stream(().sorted((Map.Entry.comparingByKey()).forEach((/*???*/);
	nodes.entrySet(().stream(().sorted((Map.Entry.comparingByKey()).forEach((/*???*/);
	nodeLists.entrySet(().stream(().sorted((Map.Entry.comparingByKey()).forEach((/*???*/);
	if (/*???*/)builder.append(("\n").append((indent);
	builder.append(("");
	return builder.toString();
}
