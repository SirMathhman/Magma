// Generated transpiled C++ from 'src\main\java\magma\compile\rule\StripRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct StripRule {String leftKey;Rule rule;String rightKey;};
Rule Strip_StripRule(Rule rule) {
	return new_???("", rule, "");
}
Rule Strip_StripRule(String left, Rule rule, String right) {
	return new_???(left, rule, right);
}
Result<Node, CompileError> lex_StripRule(String content) {
	return rule.lex(content.strip());
}
Result<String, CompileError> generate_StripRule(Node node) {
	return rule.generate(node).mapValue(/*???*/);
}
