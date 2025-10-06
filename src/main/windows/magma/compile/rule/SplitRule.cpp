// Generated transpiled C++ from 'src\main\java\magma\compile\rule\SplitRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct SplitRule {Rule leftRule;Rule rightRule;Splitter splitter;};
Rule First_SplitRule(Rule left, char* infix, Rule right) {
	Splitter splitter=new_???((infix, new_???(());
	return new_???((left, right, splitter);
}
Rule Last_SplitRule(Rule leftRule, char* infix, Rule rightRule) {
	Splitter splitter=new_???((infix, new_???(());
	return new_???((leftRule, rightRule, splitter);
}
Rule Split_SplitRule(Rule left, Splitter splitter, Rule right) {
	return new_???((left, right, splitter);
}
@Override
	public Result<> lex_SplitRule(char* input) {
	return ???;
}
@Override
	public Result<> generate_SplitRule(Node node) {
	return leftRule.generate(node).flatMap(left -> rightRule.generate(node).mapValue(right -> splitter.merge(left, right)));
}
