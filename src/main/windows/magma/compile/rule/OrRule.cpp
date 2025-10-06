// Generated transpiled C++ from 'src\main\java\magma\compile\rule\OrRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct OrRule {List<> rules;};
/*???*/ Or_OrRule(/*???*/ rules) {
	return new_???(Arrays.asList(rules));
}
Result<> lex_OrRule(/*???*/ content) {
	return foldAll(/*???*/.lex(content), /*???*/);
}
Result<> foldAll_OrRule(Function<> mapper, Supplier<> context) {
	return Accumulator.merge(rules, mapper).mapErr(/*???*/);
}
Result<> generate_OrRule(/*???*/ node) {
	return foldAll(/*???*/.generate(node), /*???*/);
}
