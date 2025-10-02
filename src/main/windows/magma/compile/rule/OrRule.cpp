// Generated transpiled C++ from 'src\main\java\magma\compile\rule\OrRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct OrRule {List<Rule> rules;};
Rule Or_OrRule(/*Rule...*/ rules) {
	/*return new OrRule(Arrays.asList(rules));*/
}
Result<Node, CompileError> lex_OrRule(char* content) {
	/*final ArrayList<CompileError> errors = new ArrayList<>();*/
	/*for (Rule rule : rules)
			switch (rule.lex(content)) {
				case Ok<Node, CompileError> ok -> {
					return ok;
				}
				case Err<Node, CompileError>(CompileError error) -> errors.add(error);
			}*/
	/*return new Err<>(new CompileError("No alternative matched for input", new StringContext(content), errors));*/
}
Result<String, CompileError> generate_OrRule(Node node) {
	/*final ArrayList<CompileError> errors = new ArrayList<>();*/
	/*for (Rule rule : rules) {
			Result<String, CompileError> res = rule.generate(node);
			if (res instanceof Ok<String, CompileError> ok) return ok;
			else if (res instanceof Err<String, CompileError>(CompileError error)) errors.add(error);
		}*/
	/*return new Err<>(new CompileError("No generator matched for node", new NodeContext(node), errors));*/
}
