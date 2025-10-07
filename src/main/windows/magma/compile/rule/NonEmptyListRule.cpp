// Generated transpiled C++ from 'src\main\java\magma\compile\rule\NonEmptyListRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct NonEmptyListRule {String key;Rule innerRule;};
Rule NonEmptyList_NonEmptyListRule(String key, Rule innerRule) {
	return new_???(key, innerRule);
}
Result<Node, CompileError> lex_NonEmptyListRule(String content) {
	return innerRule.lex(content);
}
Result<String, CompileError> generate_NonEmptyListRule(Node node) {
	return /*???*/;
}
