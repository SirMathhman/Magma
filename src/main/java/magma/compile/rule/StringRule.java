package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.NodeContext;
import magma.compile.context.TokenSequenceContext;
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
	public Result<Node, CompileError> lex(TokenSequence content) {
		if (content.isEmpty()) return new Err<Node, CompileError>(new CompileError("Content of key '" + key + "' be empty",
																																							 new TokenSequenceContext(content)));
		return new Ok<Node, CompileError>(new Node().withSlice(key, content));
	}

	@Override
	public Result<TokenSequence, CompileError> generate(Node node) {
		Option<TokenSequence> resultOption = node.findSlice(key);
		return switch (resultOption) {
			case None<TokenSequence> _ ->
					new Err<String, CompileError>(new CompileError("String '" + key + "' not present.", new NodeContext(node)));
			case Some<TokenSequence>(TokenSequence value) -> new Ok<String, CompileError>(value.value());
		};
	}
}
