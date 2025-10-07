// Generated transpiled C++ from 'src\main\java\magma\compile\rule\OrRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct OrRule {List<Rule> rules;};
Rule Or_OrRule(Rule rules) {
	return new_???(List.of(rules));
}
Result<Node, CompileError> lex_OrRule(String content) {
	return foldAll(/*???*/.lex(content), /*???*/);
}
Result<T, CompileError> foldAll_OrRule(Result<T, CompileError> (*mapper)(Rule), Supplier<Context> context) {
	return Accumulator.merge(rules, mapper).mapErr(/*???*/);
}
Result<String, CompileError> generate_OrRule(Node node) {
	return foldAll(/*???*/.generate(node), /*???*/);
}
