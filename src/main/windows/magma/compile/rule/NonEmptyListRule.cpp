// Generated transpiled C++ from 'src\main\java\magma\compile\rule\NonEmptyListRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct NonEmptyListRule {char* key;Rule innerRule;};
Rule NonEmptyList_NonEmptyListRule(char* key, Rule innerRule) {
	return /*new NonEmptyListRule(key, innerRule)*/;
}
Result<Node, CompileError> lex_NonEmptyListRule(char* content) {
	/*// Delegate lexing to inner rule*/
	return /*innerRule.lex(content)*/;
}
Result<String, CompileError> generate_NonEmptyListRule(Node node) {
	/*return switch (node.findNodeList(key)) {
			case None<?> _ -> new Err<>(new CompileError("Node list '" + key + "' not present", new NodeContext(node)));
			case Some(var list) when list.isEmpty() ->
					new Err<>(new CompileError("Node list '" + key + "' is empty", new NodeContext(node)));
			case Some<?> _ -> innerRule.generate(node);
		}*/
	/*;*/
}
