// Generated transpiled C++ from 'src\main\java\magma\compile\rule\OrRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct OrRule {/*List<Rule>*/ rules;};
struct Accumulator {/*Option<T>*/ option;/* List<CompileError>*/ errors;};
/*public*/ Accumulator_Accumulator() {
	/*this*/(/*new None<>()*/, /* new ArrayList<>())*/;
}
/*public Accumulator<T>*/ addError_Accumulator(/*CompileError*/ error) {
	/*errors.add*/(/*error)*/;
	return /*this*/;
}
/*public Accumulator<T>*/ setValue_Accumulator(/*T*/ value) {
	/*return new Accumulator<>*/(/*new Some<>(value)*/, /* errors)*/;
}
/*public Result<T, List<CompileError>>*/ toResult_Accumulator() {
	/*return switch */(/*option) {
				case None<T> _ -> new Err<>(errors);
				case Some<T> v -> new Ok<>(v.value());
			}*/;
}
/*public static Rule*/ Or_OrRule(/*Rule...*/ rules) {
	/*return new OrRule*/(/*Arrays.asList(rules))*/;
}
/*@Override
	public Result<Node, CompileError>*/ lex_OrRule(/*String*/ content) {
	/*return foldAll*/(/*rule1 -> rule1.lex(content), () -> new StringContext(content))*/;
}
/*private <T> Result<T, CompileError>*/ foldAll_OrRule(/*Function<Rule, Result<T, CompileError>>*/ mapper, /*
																							Supplier<Context>*/ context) {
	/*return rules.stream*/(/*)
								.reduce(new Accumulator<T>()*/, /* (accumulator*/, /* rule) -> switch (mapper.apply(rule)) {
									case Err<T*/, /* CompileError> v -> accumulator.addError(v.error());
									case Ok<T, CompileError> v -> accumulator.setValue(v.value());
								}, (_, next) -> next)
								.toResult()
								.mapErr(errors -> new CompileError("No alternative matched for input", context.get(), errors))*/;
}
/*@Override
	public Result<String, CompileError>*/ generate_OrRule(/*Node*/ node) {
	/*return foldAll*/(/*rule1 -> rule1.generate(node), () -> new NodeContext(node))*/;
}
