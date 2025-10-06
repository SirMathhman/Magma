// Generated transpiled C++ from 'src\main\java\magma\compile\rule\TagRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct TagRule {char* tag;Rule rule;};
Rule Tag_TagRule(char* type, Rule rule) {
	return new_???(type, rule);
}
Result<> lex_TagRule(char* content) {
	Result<> lex=rule.lex(content);
	return lex.mapValue(/*???*/.retype(tag)).mapErr(/*???*/);
}
Result<> generate_TagRule(Node node) {
	if (node.is(tag))return rule.generate(node).mapErr(/*???*/);
	else
	return new_???(new_???(""+tag+"", new_???(node)));
}
