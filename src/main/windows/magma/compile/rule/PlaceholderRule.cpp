// Generated transpiled C++ from 'src\main\java\magma\compile\rule\PlaceholderRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct PlaceholderRule {/*???*/ rule;};
/*???*/ wrap_PlaceholderRule(/*???*/ input) {
	return ""+input.replace("", "").replace("", "")+"";
}
/*???*/ Placeholder_PlaceholderRule(/*???*/ rule) {
	return new_???(rule);
}
/*???*/ lex_PlaceholderRule(/*???*/ content) {
	return rule.lex(content);
}
/*???*/ generate_PlaceholderRule(/*???*/ node) {
	return rule.generate(node).mapValue(/*???*/);
}
