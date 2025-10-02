// Generated transpiled C++ from 'src\main\java\magma\compile\rule\EmptyRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct EmptyRule {};
/*public static final Rule Empty = new*/ EmptyRule_EmptyRule() {
}
/*@Override
	public Result<Node, CompileError>*/ lex_EmptyRule(/*String*/ content) {
	/*if */(/*content.isEmpty()) return new Ok<>(new Node())*/;
	/*return new Err<>*/(/*new CompileError("Content is not empty"*/, /* new StringContext(content)))*/;
}
/*@Override
	public Result<String, CompileError>*/ generate_EmptyRule(/*Node*/ node) {
	/*return new Ok<>*/(/*"")*/;
}
