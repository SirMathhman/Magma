// Generated transpiled C++ from 'src\main\java\magma\compile\rule\TagRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct TagRule {/*???*/ tag;/*???*/ rule;};
/*???*/ Tag_TagRule(/*???*/ type, /*???*/ rule) {
	return new_???(type, rule);
}
/*???*/ lex_TagRule(/*???*/ content) {
	/*???*/ lex=rule.lex(content);
	return lex.mapValue(/*???*/.retype(tag)).mapErr(/*???*/);
}
/*???*/ generate_TagRule(/*???*/ node) {
	if (node.is(tag))return rule.generate(node).mapErr(/*???*/);
	else
	return new_???(new_???(""+tag+"", new_???(node)));
}
