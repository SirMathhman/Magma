// Generated transpiled C++ from 'src\main\java\magma\compile\rule\SuffixRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct SuffixRule {};
Rule Suffix_SuffixRule() {
	return new_???();
}
Result<Node, CompileError> lex_SuffixRule() {
	if (/*???*/.endsWith())return new_???();
	String slice=input.substring();
	return getRule().lex();
}
Result<String, CompileError> generate_SuffixRule() {
	return rule.generate().mapValue();
}
Rule getRule_SuffixRule() {
	return rule;
}
