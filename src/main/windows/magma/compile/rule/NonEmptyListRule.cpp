// Generated transpiled C++ from 'src\main\java\magma\compile\rule\NonEmptyListRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct NonEmptyListRule {/*String*/ key;/* Rule*/ innerRule;};
/*public static Rule*/ NonEmptyList_NonEmptyListRule(/*String*/ key, /* Rule*/ innerRule) {
	/*return new NonEmptyListRule*/(/*key*/, /* innerRule)*/;
}
/*@Override
	public Result<Node, CompileError>*/ lex_NonEmptyListRule(/*String*/ content) {
	// Delegate lexing to inner rule
	/*return innerRule.lex*/(/*content)*/;
}
/*@Override
	public Result<String, CompileError>*/ generate_NonEmptyListRule(/*Node*/ node) {
	/*return switch */(/*node.findNodeList(key)) {
			case None<?> _ -> new Err<>(new CompileError("Node list '" + key + "' not present", new NodeContext(node)));
			case Some(var list) when list.isEmpty() ->
					new Err<>(new CompileError("Node list '" + key + "' is empty", new NodeContext(node)));
			case Some<?> _ -> innerRule.generate(node);
		}*/;
}
