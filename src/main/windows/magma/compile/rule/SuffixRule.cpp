// Generated transpiled C++ from 'src\main\java\magma\compile\rule\SuffixRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct SuffixRule {Rule rule;char* suffix;};
Rule Suffix_SuffixRule(Rule rule, char* suffix) {
	return new_???((rule, suffix);
}
@Override
	public Result<> lex_SuffixRule(char* input) {
	if (/*???*/.endsWith(suffix()))return new_???((new_???((""+suffix+"", new_???((input)));
	char* slice=/*???*/;
	return getRule().lex(slice);
}
@Override
	public Result<> generate_SuffixRule(Node node) {
	return rule.generate(node).mapValue(value -> value + suffix());
}
Rule getRule_SuffixRule() {
	return rule;
}
