// Generated transpiled C++ from 'src\main\java\magma\compile\rule\OrRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct OrRule {};
Rule Or_OrRule() {
	return new_???();
}
Result<Node, CompileError> lex_OrRule() {
	return foldAll();
}
Result<T, CompileError> foldAll_OrRule() {
	return Accumulator.merge().mapErr();
}
Result<String, CompileError> generate_OrRule() {
	return foldAll();
}
