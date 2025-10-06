package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.StringContext;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

public class EmptyRule implements Rule {
	public static final Rule Empty = new EmptyRule();

	@Override
	public Result<Node, CompileError> lex(String content) {
		if (content.isEmpty()) return new Ok<Node, CompileError>(new Node());
		return new Err<Node, CompileError>(new CompileError("Content is not empty", new StringContext(content)));
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return new Ok<String, CompileError>("");
	}
}
