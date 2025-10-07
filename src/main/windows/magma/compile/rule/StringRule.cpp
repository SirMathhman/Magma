// Generated transpiled C++ from 'src\main\java\magma\compile\rule\StringRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct StringRule {String key;};
Rule String_StringRule(String key) {
	return new_???(key);
}
Result<Node, CompileError> lex_StringRule(String content) {
	if (content.isEmpty())return new_???(new_???(""+key+"", new_???(content)));
	return new_???(new_???(key, content));
}
Result<String, CompileError> generate_StringRule(Node node) {
	Option<Result<String, CompileError>> resultOption=node.findString(key).map(/*???*/);
	return /*???*/;
}
