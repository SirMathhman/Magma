// Generated transpiled C++ from 'src\main\java\magma\compile\rule\NodeListRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct NodeListRule {char* key;Rule rule;Divider divider;};
Rule Statements_NodeListRule(char* key, Rule rule) {
	return new_???((key, rule, new_???((new_???((new_???(())));
}
Rule Delimited_NodeListRule(char* key, Rule rule, char* delimiter) {
	return new_???((key, rule, new_???((delimiter));
}
Rule Expressions_NodeListRule(char* key, Rule rule) {
	return new_???((key, rule, new_???((new_???((new_???(())));
}
@Override
	public Result<> lex_NodeListRule(char* input) {
	return divider.divide(input).reduce(new Ok<>(new ArrayList<>()), this::fold, (_, next) -> next).mapValue(list -> new Node().withNodeList(key, list)).mapErr(err -> new CompileError("Failed to lex segments for key '" + key + "'", new StringContext(input), List.of(err)));
}
private Result<> fold_NodeListRule(Result<> current, char* element) {
	return ???;
}
@Override
	public Result<> generate_NodeListRule(Node value) {
	Option<> resultOption=value.findNodeList(key).map(list -> {
			// Treat missing or empty lists as empty content when generating.if (list.isEmpty()) return new Ok<>("");

			final StringJoiner sb = new StringJoiner(divider.delimiter());
			for (Node child : list)
				switch (this.rule.generate(child)) {
					case Ok<String, CompileError>(String value1) -> sb.add(value1);
					case Err<String, CompileError>(CompileError error) -> {
						return new Err<>(error);
					}
				}

			return new Ok<>(sb.toString());
		});
	return ???;
}
