// Generated transpiled C++ from 'src\main\java\magma\compile\rule\SplitRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct SplitRule {Rule leftRule;Rule rightRule;Splitter splitter;Order order;};
struct Order {};
Result<> evaluate_Order(char* left, char* right, Rule leftRule, Rule rightRule) {
}
struct LeftFirst {};
Result<> evaluate_LeftFirst(char* left, char* right, Rule leftRule, Rule rightRule) {
	return leftRule.lex(left).flatMap(/*???*/.lex(right).mapValue(/*???*/));
}
struct RightFirst {};
Result<> evaluate_RightFirst(char* left, char* right, Rule leftRule, Rule rightRule) {
	return rightRule.lex(right).flatMap(/*???*/.lex(left).mapValue(/*???*/));
}
Rule First_SplitRule(Rule left, char* infix, Rule right) {
	Splitter splitter=new_???(infix, new_???());
	return new_???(left, right, splitter, new_???());
}
Rule Last_SplitRule(Rule leftRule, char* infix, Rule rightRule) {
	Splitter splitter=new_???(infix, new_???());
	return new_???(leftRule, rightRule, splitter, new_???());
}
Rule Split_SplitRule(Rule left, Splitter splitter, Rule right) {
	return new_???(left, right, splitter, new_???());
}
Result<> lex_SplitRule(char* input) {
	return /*???*/;
}
Result<> evaluate_SplitRule(char* left, char* right) {
	return leftRule.lex(left).flatMap(/*???*/.lex(right).mapValue(/*???*/));
}
Result<> generate_SplitRule(Node node) {
	return leftRule.generate(node).flatMap(/*???*/.generate(node).mapValue(/*???*/.merge(left, right)));
}
