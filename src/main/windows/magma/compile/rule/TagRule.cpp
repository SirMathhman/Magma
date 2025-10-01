// Generated transpiled C++ from 'src\main\java\magma\compile\rule\TagRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct TagRule<>{};
Rule Tag_TagRule(char* type, Rule rule) {/*
		return new TagRule(type, rule);
	*/}
/*CompileError>*/ lex_TagRule(char* content) {/*
		return rule.lex(content)
							 .mapValue(node -> node.retype(tag))
							 .mapErr(error -> new CompileError("Failed to attach tag '" + tag + "'",
																								 new StringContext(content),
																								 List.of(error)));
	*/}
/*CompileError>*/ generate_TagRule(Node node) {/*
		if (node.is(tag)) return rule.generate(node);
		else return new Err<>(new CompileError("Type '" + tag + "' not present", new NodeContext(node)));
	*/}
