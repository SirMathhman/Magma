// Generated transpiled C++ from 'src\main\java\magma\compile\rule\SuffixRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct SuffixRule {Rule rule;String suffix;};
Rule Suffix_SuffixRule(Rule rule, String suffix) {
	return new_???(rule, suffix);
}
Result<Node, CompileError> lex_SuffixRule(String input) {
	if (/*???*/.endsWith(suffix()))return new_???(new_???(""+suffix+"", new_???(input)));
	String slice=input.substring(/*???*/, /*???*/);
	return getRule().lex(slice);
}
Result<String, CompileError> generate_SuffixRule(Node node) {
	return rule.generate(node).mapValue(/*???*/+suffix());
}
Rule getRule_SuffixRule() {
	return rule;
}
