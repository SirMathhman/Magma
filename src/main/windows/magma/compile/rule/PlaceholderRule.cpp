// Generated transpiled C++ from 'src\main\java\magma\compile\rule\PlaceholderRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct PlaceholderRule {Rule rule;};
char* wrap_PlaceholderRule(char* input) {
	return ""+input.replace("", "").replace("", "")+"";
}
Rule Placeholder_PlaceholderRule(Rule rule) {
	return new_???(rule);
}
Result<> lex_PlaceholderRule(char* content) {
	return rule.lex(content);
}
Result<> generate_PlaceholderRule(Node node) {
	return rule.generate(node).mapValue(/*???*/);
}
