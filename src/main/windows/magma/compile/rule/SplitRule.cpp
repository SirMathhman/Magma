// Generated transpiled C++ from 'src\main\java\magma\compile\rule\SplitRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct SplitRule {Rule leftRule;Rule rightRule;Splitter splitter;Order order;};
struct Order {};
Result<Node, CompileError> evaluate_Order(String left, String right, Rule leftRule, Rule rightRule) {
}
struct LeftFirst {};
Result<Node, CompileError> evaluate_LeftFirst(String left, String right, Rule leftRule, Rule rightRule) {
	return leftRule.lex(left).flatMap(/*???*/.lex(right).mapValue(/*???*/));
}
struct RightFirst {};
Result<Node, CompileError> evaluate_RightFirst(String left, String right, Rule leftRule, Rule rightRule) {
	return rightRule.lex(right).flatMap(/*???*/.lex(left).mapValue(/*???*/));
}
Rule First_SplitRule(Rule left, String infix, Rule right) {
	Splitter splitter=new_???(infix, new_???());
	return new_???(left, right, splitter, new_???());
}
Rule Last_SplitRule(Rule leftRule, String infix, Rule rightRule) {
	Splitter splitter=new_???(infix, new_???());
	return new_???(leftRule, rightRule, splitter, new_???());
}
Rule Split_SplitRule(Rule left, Splitter splitter, Rule right) {
	return new_???(left, right, splitter, new_???());
}
Result<Node, CompileError> lex_SplitRule(String input) {
	return /*???*/;
}
Result<Node, CompileError> evaluate_SplitRule(String left, String right) {
	return leftRule.lex(left).flatMap(/*???*/.lex(right).mapValue(/*???*/));
}
Result<String, CompileError> generate_SplitRule(Node node) {
	return leftRule.generate(node).flatMap(/*???*/.generate(node).mapValue(/*???*/.merge(left, right)));
}
