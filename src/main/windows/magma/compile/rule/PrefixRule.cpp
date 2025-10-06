// Generated transpiled C++ from 'src\main\java\magma\compile\rule\PrefixRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct PrefixRule {char* prefix;Rule rule;};
Rule Prefix_PrefixRule(char* prefix, Rule rule) {
	return new_???((prefix, rule);
}
@Override
	public Result<> lex_PrefixRule(char* content) {
	return rule.lex(content.substring(prefix.length()));
	else
	return new_???((new_???((""+prefix+"", new_???((content)));
}
@Override
	public Result<> generate_PrefixRule(Node node) {
	return rule.generate(node).mapValue(inner -> prefix + inner);
}
