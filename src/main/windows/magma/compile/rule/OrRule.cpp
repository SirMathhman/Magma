// Generated transpiled C++ from 'src\main\java\magma\compile\rule\OrRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct OrRule {/*List<Rule>*/ rules;};
/*public static Rule*/ Or_OrRule(/*Rule...*/ rules) {
	/*return new OrRule*/(/*Arrays.asList(rules))*/;
}
/*@Override
	public Result<Node, CompileError>*/ lex_OrRule(/*String*/ content) {
	/*return foldAll*/(/*rule1 -> rule1.lex(content), () -> new StringContext(content))*/;
}
/*private <T> Result<T, CompileError>*/ foldAll_OrRule(/*Function<Rule, Result<T, CompileError>>*/ mapper, /*
																							Supplier<Context>*/ context) {
	/*return Accumulator.merge*/(/*rules*/, /* mapper)
											.mapErr(errors -> new CompileError("No alternative matched for input", context.get(), errors))*/;
}
/*@Override
	public Result<String, CompileError>*/ generate_OrRule(/*Node*/ node) {
	/*return foldAll*/(/*rule1 -> rule1.generate(node), () -> new NodeContext(node))*/;
}
