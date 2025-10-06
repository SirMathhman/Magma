// Generated transpiled C++ from 'src\main\java\magma\compile\rule\NodeRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct NodeRule {char* key;Rule rule;};
Rule Node_NodeRule(char* key, Rule rule) {
	return new_???((key, rule);
}
@Override
	public Result<> lex_NodeRule(char* content) {
	return rule.lex(content).mapValue(node -> new Node().withNode(key, node));
}
@Override
	public Result<> generate_NodeRule(Node node) {
	return ???;
}
