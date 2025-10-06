// Generated transpiled C++ from 'src\main\java\magma\compile\rule\NonEmptyListRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct NonEmptyListRule {char* key;Rule innerRule;};
Rule NonEmptyList_NonEmptyListRule(char* key, Rule innerRule) {
	return new_NonEmptyListRule((key, innerRule);
}
@Override
	public Result<> lex_NonEmptyListRule(char* content) {
	return innerRule.lex(content);
}
@Override
	public Result<> generate_NonEmptyListRule(Node node) {
	return ???;
}
