// Generated transpiled C++ from 'src\main\java\magma\compile\rule\NodeRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct NodeRule {/*???*/ key;/*???*/ rule;};
/*???*/ Node_NodeRule(/*???*/ key, /*???*/ rule) {
	return new_???(key, rule);
}
/*???*/ lex_NodeRule(/*???*/ content) {
	return rule.lex(content).mapValue(/*???*/.withNode(key, node)).mapErr(/*???*/);
}
/*???*/ generate_NodeRule(/*???*/ node) {
	return /*???*/;
}
