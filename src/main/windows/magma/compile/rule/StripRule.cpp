// Generated transpiled C++ from 'src\main\java\magma\compile\rule\StripRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct StripRule {/*???*/ leftKey;/*???*/ rule;/*???*/ rightKey;};
/*???*/ Strip_StripRule(/*???*/ rule) {
	return new_???("", rule, "");
}
/*???*/ Strip_StripRule(/*???*/ left, /*???*/ rule, /*???*/ right) {
	return new_???(left, rule, right);
}
Result</*???*/, /*???*/> lex_StripRule(/*???*/ content) {
	return rule.lex(content.strip());
}
Result</*???*/, /*???*/> generate_StripRule(/*???*/ node) {
	return rule.generate(node).mapValue(/*???*/);
}
