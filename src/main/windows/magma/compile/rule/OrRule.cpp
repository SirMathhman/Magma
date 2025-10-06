// Generated transpiled C++ from 'src\main\java\magma\compile\rule\OrRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct OrRule {List<> rules;};
Rule Or_OrRule(/*???*/ rules) {
	new OrRule(Arrays.asList(rules));
}
@Override
	public Result<> lex_OrRule(char* content) {
	return foldAll((/*???*/.lex(content), /*???*/);
}
Result<> foldAll_OrRule(Result<> (*mapper)(Rule), Supplier<> context) {
	return Accumulator.merge(rules, mapper).mapErr(errors -> new CompileError("No alternative matched for input", context.get(), errors));
}
@Override
	public Result<> generate_OrRule(Node node) {
	return foldAll((/*???*/.generate(node), /*???*/);
}
