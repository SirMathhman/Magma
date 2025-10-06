// Generated transpiled C++ from 'src\main\java\magma\compile\Node.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Node {int MAX_FORMAT_LEVEL;Map<> nodeLists;Map<> nodes;Map<> strings;Option<> maybeType;};
char* escape_Node(char* value) {
	return value.replace("", "").replace("", "").replace("", "").replace("", "").replace("", "");
}
char* toString_Node() {
	return format(0);
}
Node withString_Node(char* key, char* value) {
	strings.put(key, value);
	return this;
}
Option<> findString_Node(char* key) {
	return Option.ofNullable(strings.get(key));
}
Node merge_Node(Node node) {
	maybeType=/*???*/;
	this.strings.putAll(node.strings);
	nodeLists.putAll(node.nodeLists);
	nodes.putAll(node.nodes);
	return this;
}
Node withNodeList_Node(char* key, List<> values) {
	nodeLists.put(key, values);
	return this;
}
Option<> findNodeList_Node(char* key) {
	return Option.ofNullable(nodeLists.get(key));
}
Node withNode_Node(char* key, Node node) {
	nodes.put(key, node);
	return this;
}
Option<> findNode_Node(char* key) {
	return Option.ofNullable(nodes.get(key));
}
Node retype_Node(char* type) {
	this.maybeType=Option.of(type);
	return this;
}
boolean is_Node(char* type) {
	return this.maybeType.map(/*???*/.equals(type)).orElse(false);
}
Set<> getStringKeys_Node() {
	return strings.keySet();
}
char* format_Node(int depth) {
	return format(depth, MAX_FORMAT_LEVEL);
}
char* format_Node(int depth, int maxLevel) {
	char* indent="".repeat(depth);
	return indent+appendJsonPure(depth, 0, maxLevel);
}
char* appendJsonPure_Node(int indentDepth, int level, int maxLevel) {
	char* indent="".repeat(indentDepth);
	char* childIndent="".repeat(indentDepth+1);
	StringBuilder builder=new_???();
	builder.append("");
	boolean* hasFields=/*???*/;
	/*???*/=maybeType;
	if (/*???*/)
	{
	builder.append("").append(childIndent).append("").append(escape(value)).append("");
	/*???*/=true;}
	strings.entrySet().stream().sorted(Entry.comparingByKey()).forEach(/*???*/);
	nodes.entrySet().stream().sorted(Entry.comparingByKey()).forEach(/*???*/);
	nodeLists.entrySet().stream().sorted(Entry.comparingByKey()).forEach(/*???*/);
	if (/*???*/)builder.append("").append(indent);
	builder.append("");
	return builder.toString();
}
void extracted1_Node(int indentDepth, int level, int maxLevel, Entry<> entry, boolean* hasFields, StringBuilder builder, char* childIndent) {
	if (/*???*/)builder.append("");
	else
	builder.append("");
	builder.append(childIndent).append('"').append(escape(entry.getKey())).append("");
	extracted(indentDepth, level, maxLevel, entry, builder, childIndent).append("");
	/*???*/=true;
}
StringBuilder extracted_Node(int indentDepth, int level, int maxLevel, Entry<> entry, StringBuilder builder, char* childIndent) {
	/*???*/=entry.getValue();
	return builder;
	if (/*???*/)
	{
	builder.append("");
	return builder;}
	builder.append("");
	builder.append(list.stream().map(/*???*/(indentDepth, level, maxLevel, node)).collect(Collectors.joining("")));
	builder.append("").append(childIndent);
	return builder;
}
char* getString_Node(int indentDepth, int level, int maxLevel, Node node) {
	char* repeat="".repeat(indentDepth+2);
	char* s=node.appendJsonPure(indentDepth+2, level+1, maxLevel);
	return repeat+s;
}
void extracted_Node(int indentDepth, int level, int maxLevel, Entry<> entry, boolean* hasFields, StringBuilder builder, char* childIndent) {
	if (/*???*/)builder.append("");
	else
	builder.append("");
	builder.append(childIndent).append('"').append(escape(entry.getKey())).append("");
	if (/*???*/)builder.append(entry.getValue().appendJsonPure(indentDepth+1, level+1, maxLevel));
	else
	builder.append("");
	/*???*/=true;
}
void extracted_Node(Entry<> entry, boolean* hasFields, StringBuilder builder, char* childIndent) {
	if (/*???*/)builder.append("");
	else
	builder.append("");
	builder.append(childIndent).append('"').append(escape(entry.getKey())).append("").append(escape(entry.getValue())).append('"');
	/*???*/=true;
}
