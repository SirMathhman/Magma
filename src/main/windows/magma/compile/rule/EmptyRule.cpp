// Generated transpiled C++ from 'src\main\java\magma\compile\rule\EmptyRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct EmptyRule {};
new EmptyRule_EmptyRule() {
}
@Override
	public Result<> lex_EmptyRule(char* content) {
	if (content.isEmpty())return new_Ok_((new_Node(());
	return new_Err_((new_CompileError(("", new_StringContext((content)));
}
@Override
	public Result<> generate_EmptyRule(Node node) {
	new Ok<>("");
}
