// Generated transpiled C++ from 'src\main\java\magma\compile\rule\TagRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct TagRule {};
Rule Tag_TagRule() {
	return new_???();
}
Result<Node, CompileError> lex_TagRule() {
	Result<Node, CompileError> lex=rule.lex();
	return lex.mapValue().mapErr();
}
Result<String, CompileError> generate_TagRule() {
	if (node.is())return rule.generate().mapErr();
	else
	return new_???();
}
