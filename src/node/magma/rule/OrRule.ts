/*import magma.error.CompileError;*/
/*import magma.node.Node;*/
/*import magma.result.Err;*/
/*import magma.result.Ok;*/
/*import magma.result.Result;*/
/*import java.util.List;*/
/*import java.util.Optional;*/
/*public record OrRule(List<Rule> rules) implements Rule {
	private Optional<Node> lex0(final String input) {
		return this.rules.stream().map(rule -> rule.lex(input).findValue()).flatMap(Optional::stream).findFirst();
	}

	private Optional<String> generate0(final Node node) {
		return this.rules.stream().map(rule -> rule.generate(node).findValue()).flatMap(Optional::stream).findFirst();
	}

	@Override
	public Result<Node, CompileError> lex(final String input) {
		return this.lex0(input).<Result<Node, CompileError>>map(Ok::new).orElseGet(() -> new Err<>(new CompileError()));
	}

	@Override
	public Result<String, CompileError> generate(final Node node) {
		return this.generate0(node).<Result<String, CompileError>>map(Ok::new).orElseGet(() -> new Err<>(new CompileError()));
	}
}*/
