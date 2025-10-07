// Generated transpiled C++ from 'src\main\java\magma\compile\rule\PlaceholderRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct PlaceholderRule {Rule rule;};
String wrap_PlaceholderRule(String input) {
	return ""+input.replace("", "").replace("", "")+"";
}
Rule Placeholder_PlaceholderRule(Rule rule) {
	return new_???(rule);
}
Result<Node, CompileError> lex_PlaceholderRule(String content) {
	return rule.lex(content);
}
Result<String, CompileError> generate_PlaceholderRule(Node node) {
	return rule.generate(node).mapValue(/*???*/);
}
