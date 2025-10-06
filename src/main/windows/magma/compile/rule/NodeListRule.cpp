// Generated transpiled C++ from 'src\main\java\magma\compile\rule\NodeListRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct NodeListRule {char* key;Rule rule;Divider divider;};
Rule Statements_NodeListRule(char* key, Rule rule) {
	return new_???(key, rule, new_???(new_???(new_???())));
}
Rule Delimited_NodeListRule(char* key, Rule rule, char* delimiter) {
	return new_???(key, rule, new_???(delimiter));
}
Rule Expressions_NodeListRule(char* key, Rule rule) {
	return new_???(key, rule, new_???(new_???(new_???())));
}
Result<> lex_NodeListRule(char* input) {
	return divider.divide(input).reduce(new_???(new_???()), /*???*/, /*???*/).mapValue(/*???*/.withNodeList(key, list)).mapErr(/*???*/);
}
Result<> fold_NodeListRule(Result<> current, char* element) {
	return /*???*/;
}
Result<> generate_NodeListRule(Node value) {
	Option<> resultOption=value.findNodeList(key).map(/*???*/);
	return /*???*/;
}
Result<> generateList_NodeListRule(List<> list) {
	if (list.isEmpty())return new_???("");
	StringJoiner sb=new_???(divider.delimiter());
	int i=/*???*/;
	while (/*???*/)
	{
	Node child=list.get(i);/*???*/
	i++;}
	return new_???(sb.toString());
}
