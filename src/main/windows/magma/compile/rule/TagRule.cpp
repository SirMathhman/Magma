// Generated transpiled C++ from 'src\main\java\magma\compile\rule\TagRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<>
struct TagRule{char* tag;, Rule rule;};
template<>
/*public static Rule*/ Tag_TagRule(char* type, Rule rule) {/*
		return new TagRule(type, rule);
	*/}
template<>
@Override
	public Result<Node, CompileError> lex_TagRule(char* content) {/*
		return rule.lex(content)
							 .mapValue(node -> node.retype(tag))
							 .mapErr(error -> new CompileError("Failed to attach tag '" + tag + "'",
																								 new StringContext(content),
																								 List.of(error)));
	*/}
template<>
@Override
	public Result<String, CompileError> generate_TagRule(Node node) {/*
		if (node.is(tag)) return rule.generate(node);
		else return new Err<>(new CompileError("Type '" + tag + "' not present", new NodeContext(node)));
	*/}
