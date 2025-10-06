// Generated transpiled C++ from 'src\main\java\magma\compile\rule\StripRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct StripRule {char* leftKey;Rule rule;char* rightKey;};
Rule Strip_StripRule(Rule rule) {
	return new_???(("", rule, "");
}
Rule Strip_StripRule(char* left, Rule rule, char* right) {
	return new_???((left, rule, right);
}
@Override
	public Result<> lex_StripRule(char* content) {
	return rule.lex(content.strip());
}
@Override
	public Result<> generate_StripRule(Node node) {
	char* leftString=node.findString(leftKey).orElse("");
			final String rightString = node.findString(rightKey).orElse(""); return leftString + generated + rightString;
		});
}
