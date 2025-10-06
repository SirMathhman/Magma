// Generated transpiled C++ from 'src\main\java\magma\compile\Node.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Node {/*???*/ MAX_FORMAT_LEVEL;Map<> nodeLists;Map<> nodes;Map<> strings;Option<> maybeType;};
/*???*/ escape_Node(/*???*/ value) {
	return value.replace("", "").replace("", "").replace("", "").replace("", "").replace("", "");
}
/*???*/ toString_Node() {
	return format(/*???*/);
}
/*???*/ withString_Node(/*???*/ key, /*???*/ value) {
	strings.put(key, value);
	/*???*/ this;
}
Option<> findString_Node(/*???*/ key) {
	return Option.ofNullable(strings.get(key));
}
/*???*/ merge_Node(/*???*/ node) {
	maybeType=/*???*/;
	this.strings.putAll(node.strings);
	nodeLists.putAll(node.nodeLists);
	nodes.putAll(node.nodes);
	/*???*/ this;
}
/*???*/ withNodeList_Node(/*???*/ key, List<> values) {
	nodeLists.put(key, values);
	/*???*/ this;
}
Option<> findNodeList_Node(/*???*/ key) {
	return Option.ofNullable(nodeLists.get(key));
}
/*???*/ withNode_Node(/*???*/ key, /*???*/ node) {
	nodes.put(key, node);
	/*???*/ this;
}
Option<> findNode_Node(/*???*/ key) {
	return Option.ofNullable(nodes.get(key));
}
/*???*/ retype_Node(/*???*/ type) {
	this.maybeType==Option.of(type);
	/*???*/ this;
}
/*???*/ is_Node(/*???*/ type) {
	return this.maybeType.map(/*???*/.equals(type)).orElse(false);
}
Set<> getStringKeys_Node() {
	return strings.keySet();
}
/*???*/ format_Node(/*???*/ depth) {
	return format(depth, MAX_FORMAT_LEVEL);
}
/*???*/ format_Node(/*???*/ depth, /*???*/ maxLevel) {
	/*???*/ indent="".repeat(depth);
	return indent+appendJsonPure(depth, /*???*/, maxLevel);
}
/*???*/ appendJsonPure_Node(/*???*/ indentDepth, /*???*/ level, /*???*/ maxLevel) {
	/*???*/ indent="".repeat(indentDepth);
	/*???*/ childIndent="".repeat(indentDepth+/*???*/);
	/*???*/ builder=new_???();
	builder.append("");
	/*???*/* hasFields=/*???*/;
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
/*???*/ extracted1_Node(/*???*/ indentDepth, /*???*/ level, /*???*/ maxLevel, Entry<> entry, /*???*/* hasFields, /*???*/ builder, /*???*/ childIndent) {
	if (/*???*/)builder.append("");
	else
	builder.append("");
	builder.append(childIndent).append('"').append(escape(entry.getKey())).append("");
	extracted(indentDepth, level, maxLevel, entry, builder, childIndent).append("");
	/*???*/=true;
}
/*???*/ extracted_Node(/*???*/ indentDepth, /*???*/ level, /*???*/ maxLevel, Entry<> entry, /*???*/ builder, /*???*/ childIndent) {
	/*???*/();
	/*???*/ builder;
	if (/*???*/)
	{
	builder.append("");
	/*???*/ builder;}
	builder.append("");
	builder.append(list.stream().map(/*???*/(indentDepth, level, maxLevel, node)).collect(new_???("")));
	builder.append("").append(childIndent);
	/*???*/ builder;
}
/*???*/ getString_Node(/*???*/ indentDepth, /*???*/ level, /*???*/ maxLevel, /*???*/ node) {
	/*???*/ repeat="".repeat(indentDepth+/*???*/);
	/*???*/ s=node.appendJsonPure(indentDepth+/*???*/, level+/*???*/, maxLevel);
	return repeat+s;
}
/*???*/ extracted_Node(/*???*/ indentDepth, /*???*/ level, /*???*/ maxLevel, Entry<> entry, /*???*/* hasFields, /*???*/ builder, /*???*/ childIndent) {
	if (/*???*/)builder.append("");
	else
	builder.append("");
	builder.append(childIndent).append('"').append(escape(entry.getKey())).append("");
	if (/*???*/)builder.append(entry.getValue().appendJsonPure(indentDepth+/*???*/, level+/*???*/, maxLevel));
	else
	builder.append("");
	/*???*/=true;
}
/*???*/ extracted_Node(Entry<> entry, /*???*/* hasFields, /*???*/ builder, /*???*/ childIndent) {
	if (/*???*/)builder.append("");
	else
	builder.append("");
	builder.append(childIndent).append('"').append(escape(entry.getKey())).append("").append(escape(entry.getValue())).append('"');
	/*???*/=true;
}
