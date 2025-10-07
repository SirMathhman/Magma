// Generated transpiled C++ from 'src\main\java\magma\compile\rule\ContextRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct ContextRule {String whenErr;Rule child;};
Result<Node, CompileError> lex_ContextRule(String content) {
	return child.lex(content).mapErr(/*???*/);
}
Result<String, CompileError> generate_ContextRule(Node node) {
	return child.generate(node).mapErr(/*???*/);
}
