// Generated transpiled C++ from 'src\main\java\magma\compile\rule\PrefixRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct PrefixRule {/*???*/ prefix;/*???*/ rule;};
/*???*/ Prefix_PrefixRule(/*???*/ prefix, /*???*/ rule) {
	return new_???(prefix, rule);
}
Result</*???*/, /*???*/> lex_PrefixRule(/*???*/ content) {
	if (content.startsWith(prefix))return rule.lex(content.substring(prefix.length()));
	else
	return new_???(new_???(""+prefix+"", new_???(content)));
}
Result</*???*/, /*???*/> generate_PrefixRule(/*???*/ node) {
	return rule.generate(node).mapValue(/*???*/+inner);
}
