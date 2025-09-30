package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.NodeContext;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

public record StringRule(String key) implements Rule {
	public static Rule String(String key) {
		return new StringRule(key);
	}

	@Override
	public Result<Node, CompileError> lex(String content) {
		return new Ok<>(new Node().withString(getKey(), content));
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return node.findString(key)
							 .<Result<String, CompileError>>map(Ok::new)
							 .orElseGet(
									 () -> new Err<>(new CompileError("String '" + key + "' not present.", new NodeContext(node))));
	}

	public String getKey() {
		return key;
	}
}
