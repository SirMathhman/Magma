/*import magma.error.CompileError;*/
/*import magma.node.MapNode;*/
/*import magma.node.Node;*/
/*import magma.result.Err;*/
/*import magma.result.Ok;*/
/*import magma.result.Result;*/
/*import java.util.Optional;*/
/*public record StringRule(String key) implements Rule {
	private Optional<Node> lex0(final String input) {
		return Optional.of(new MapNode().withString(this.key, input));
	}

	private Optional<String> generate0(final Node node) {
		return node.findString(this.key);
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
