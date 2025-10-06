// Generated transpiled C++ from 'src\main\java\magma\compile\rule\StringRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct StringRule {/*???*/ key;};
/*???*/ String_StringRule(/*???*/ key) {
	return new_???(key);
}
Result<> lex_StringRule(/*???*/ content) {
	if (content.isEmpty())return new_???(new_???(""+key+"", new_???(content)));
	return new_???(new_???(key, content));
}
Result<> generate_StringRule(/*???*/ node) {
	Option<> resultOption=node.findString(key).map(/*???*/);
	return /*???*/;
}
