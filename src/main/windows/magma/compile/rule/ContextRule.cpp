// Generated transpiled C++ from 'src\main\java\magma\compile\rule\ContextRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct ContextRule {};
Result<Node, CompileError> lex_ContextRule() {
	return child.lex().mapErr();
}
Result<String, CompileError> generate_ContextRule() {
	return child.generate().mapErr();
}
