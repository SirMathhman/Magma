package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.InputContext;
import magma.compile.context.NodeContext;
import magma.compile.error.CompileError;
import magma.list.List;
import magma.result.Result;

public record ContextRule(String whenErr, Rule child) implements Rule {
	@Override
	public Result<Node, CompileError> lex(TokenSequence content) {
		return child.lex(content).mapErr(err -> new CompileError(whenErr, new InputContext(content), List.of(err)));
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return child.generate(node).mapErr(err -> new CompileError(whenErr, new NodeContext(node), List.of(err)));
	}
}
