// Generated transpiled C++ from 'src\main\java\magma\compile\rule\PlaceholderRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct PlaceholderRule {Rule rule;};
char* wrap_PlaceholderRule(char* input) {
	return ""+input.replace("/*", "start").replace("*/", "end")+"";
}
Rule Placeholder_PlaceholderRule(Rule rule) {
	new PlaceholderRule(rule);
}
@Override
	public Result<> lex_PlaceholderRule(char* content) {
	return rule.lex(content);
}
@Override
	public Result<> generate_PlaceholderRule(Node node) {
	return rule.generate(node).mapValue(PlaceholderRule::wrap);
}
