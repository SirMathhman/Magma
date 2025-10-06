// Generated transpiled C++ from 'src\main\java\magma\compile\rule\StringRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct StringRule {char* key;};
Rule String_StringRule(char* key) {
	new StringRule(key);
}
@Override
	public Result<> lex_StringRule(char* content) {
	if (content.isEmpty())return new_???((new_???((""+key+"", new_???((content)));
	return new_???((new_???(().withString(key, content));
}
@Override
	public Result<> generate_StringRule(Node node) {
	Option<> resultOption=node.findString(key).map(Ok::new);
	return ???;
}
