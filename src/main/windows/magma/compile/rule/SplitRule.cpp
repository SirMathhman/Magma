// Generated transpiled C++ from 'src\main\java\magma\compile\rule\SplitRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct SplitRule {};
struct Order {};
Result<Node, CompileError> evaluate_Order() {/*???*/
}
struct LeftFirst {};
Result<Node, CompileError> evaluate_LeftFirst() {
	return leftRule.lex().flatMap();
}
struct RightFirst {};
Result<Node, CompileError> evaluate_RightFirst() {
	return rightRule.lex().flatMap();
}
Rule First_SplitRule() {
	Splitter splitter=new_???();
	return new_???();
}
Rule Last_SplitRule() {
	Splitter splitter=new_???();
	return new_???();
}
Rule Split_SplitRule() {
	return new_???();
}
Result<Node, CompileError> lex_SplitRule() {
	return /*???*/;
}
Result<String, CompileError> generate_SplitRule() {
	return leftRule.generate().flatMap();
}
