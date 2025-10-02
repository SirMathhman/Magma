// Generated transpiled C++ from 'src\main\java\magma\compile\rule\StringRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct StringRule {char* key;};
Rule String_StringRule(char* key) {
	return /*new StringRule(key)*/;
}
Result<Node, CompileError> lex_StringRule(char* content) {
	if (/*content.isEmpty())*/)
	return /*new Err<>(new CompileError("Content of key '" + key + "' be empty", new StringContext(content))))*/;
	return /*new Ok<>(new Node().withString(key, content))*/;
}
Result<String, CompileError> generate_StringRule(Node node) {
	/*Option<Result<String, CompileError>> resultOption = node.findString(key).map(Ok::new);*/
	/*return switch (resultOption) {
			case None<Result<String, CompileError>> _ -> new Err<>(
					new CompileError("String '" + key + "' not present.", new NodeContext(node)));
			case Some<Result<String, CompileError>>(Result<String, CompileError> value) -> value;
		}*/
	/*;*/
}
