package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.NodeContext;
import magma.compile.context.StringContext;
import magma.compile.error.CompileError;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

public record StringRule(String key) implements Rule {
	public static Rule String(String key) {
		return new StringRule(key);
	}

	@Override
	public Result<Node, CompileError> lex(String content) {
		if (content.isEmpty()) return new Err<>(new CompileError("Content of key '" + key + "' be empty", new StringContext(content)));
		return new Ok<>(new Node().withString(key, content));
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		Option<Result<String, CompileError>> resultOption = node.findString(key).map(Ok::new);
		return switch (resultOption) {
			case None<Result<String, CompileError>> _ -> new Err<>(
					new CompileError("String '" + key + "' not present.", new NodeContext(node)));
			case Some<Result<String, CompileError>>(Result<String, CompileError> value) -> value;
		};
	}
}
