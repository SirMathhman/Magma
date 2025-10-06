// Generated transpiled C++ from 'src\main\java\magma\compile\rule\ContextRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct ContextRule {char* whenErr;Rule child;};
@Override
	public Result<> lex_ContextRule(char* content) {
	return child.lex(content).mapErr(err -> new CompileError(whenErr, new StringContext(content), List.of(err)));
}
@Override
	public Result<> generate_ContextRule(Node node) {
	return child.generate(node).mapErr(err -> new CompileError(whenErr, new NodeContext(node), List.of(err)));
}
