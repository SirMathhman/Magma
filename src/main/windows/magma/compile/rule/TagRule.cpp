// Generated transpiled C++ from 'src\main\java\magma\compile\rule\TagRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct TagRule {char* tag;Rule rule;};
Rule Tag_TagRule(char* type, Rule rule) {
	return new_???((type, rule);
}
@Override
	public Result<> lex_TagRule(char* content) {
	return rule.lex(content).mapValue(node -> node.retype(tag)).mapErr(error -> new CompileError("Failed to attach tag '" + tag + "'",
																								 new StringContext(content),
																								 List.of(error)));
}
@Override
	public Result<> generate_TagRule(Node node) {
	if (node.is(tag))return rule.generate(node).mapErr(error -> new CompileError("Failed to generate with tag '" + tag + "'",
																																	 new NodeContext(node),
																																	 List.of(error)));
	else
	return new_???((new_???((""+tag+"", new_???((node)));
}
