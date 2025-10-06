package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.NodeContext;
import magma.compile.context.StringContext;
import magma.compile.error.CompileError;
import magma.result.Result;

import java.util.List;

public record ContextRule(String whenErr, Rule child) implements Rule {
	@Override
	public Result<Node, CompileError> lex(String content) {
		return child.lex(content).mapErr(err -> new CompileError(whenErr, new StringContext(content), List.of(err)));
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return child.generate(node).mapErr(err -> new CompileError(whenErr, new NodeContext(node), List.of(err)));
	}
}
