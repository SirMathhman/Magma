// Generated transpiled C++ from 'src\main\java\magma\compile\rule\OrRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct OrRule {/*???*/ rules;};
/*???*/ Or_OrRule(/*???*/ rules) {
	return new_???(Arrays.asList(rules));
}
/*???*/ lex_OrRule(/*???*/ content) {
	return foldAll(/*???*/.lex(content), /*???*/);
}
/*???*/ foldAll_OrRule(/*???*/ mapper, /*???*/ context) {
	return Accumulator.merge(rules, mapper).mapErr(/*???*/);
}
/*???*/ generate_OrRule(/*???*/ node) {
	return foldAll(/*???*/.generate(node), /*???*/);
}
