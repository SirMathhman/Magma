// Generated transpiled C++ from 'src\main\java\magma\compile\rule\NodeRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct NodeRule {String key;Rule rule;};
Rule Node_NodeRule(String key, Rule rule) {
	return new_???(key, rule);
}
Result<Node, CompileError> lex_NodeRule(String content) {
	return rule.lex(content).mapValue(/*???*/.withNode(key, node)).mapErr(/*???*/);
}
Result<String, CompileError> generate_NodeRule(Node node) {
	return /*???*/;
}
