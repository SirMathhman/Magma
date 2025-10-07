// Generated transpiled C++ from 'src\main\java\magma\compile\rule\NodeListRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct NodeListRule {String key;Rule rule;Divider divider;};
Rule Statements_NodeListRule(String key, Rule rule) {
	return new_???(key, rule, new_???(new_???(new_???())));
}
Rule Delimited_NodeListRule(String key, Rule rule, String delimiter) {
	return new_???(key, rule, new_???(delimiter));
}
Rule Expressions_NodeListRule(String key, Rule rule) {
	return new_???(key, rule, new_???(new_???(new_???())));
}
Result<Node, CompileError> lex_NodeListRule(String input) {
	return divider.divide(input).reduce(new_???(new_???()), /*???*/).mapValue(/*???*/.withNodeList(key, list)).mapErr(/*???*/);
}
Result<List<Node>, CompileError> fold_NodeListRule(Result<List<Node>, CompileError> current, String element) {
	return /*???*/;
}
Result<String, CompileError> generate_NodeListRule(Node value) {
	Option<Result<String, CompileError>> resultOption=value.findNodeList(key).map(/*???*/);
	return /*???*/;
}
Result<String, CompileError> generateList_NodeListRule(List<Node> list) {
	if (list.isEmpty())return new_???("");
	StringJoiner sb=new_???(divider.delimiter());
	int i=/*???*/;
	while (/*???*/)
	{
	Node child=list.get(i).orElse(null);/*???*/
	i++;}
	return new_???(sb.toString());
}
