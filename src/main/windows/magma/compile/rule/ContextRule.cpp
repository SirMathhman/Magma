// Generated transpiled C++ from 'src\main\java\magma\compile\rule\ContextRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct ContextRule {/*???*/ whenErr;/*???*/ child;};
/*???*/ lex_ContextRule(/*???*/ content) {
	return child.lex(content).mapErr(/*???*/);
}
/*???*/ generate_ContextRule(/*???*/ node) {
	return child.generate(node).mapErr(/*???*/);
}
