/*import magma.error.CompileError;*/
/*import magma.node.Node;*/
/*import magma.result.Err;*/
/*import magma.result.Ok;*/
/*import magma.result.Result;*/
/*import java.util.Optional;*/
/*public record SuffixRule(Rule rule, String suffix) implements Rule {
	private Optional<Node> lex0(final String input) {
		if (input.endsWith(this.suffix)) {
			final var slice = input.substring(0, input.length() - this.suffix.length());
			return this.rule.lex(slice).findValue();
		} else return Optional.empty();
	}

	private Optional<String> generate0(final Node node) {
		return this.rule.generate(node).findValue().map(result -> result + this.suffix);
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
