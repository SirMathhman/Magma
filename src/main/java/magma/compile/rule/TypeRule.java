package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.NodeContext;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Result;

public record TypeRule(String type, Rule rule) implements Rule {
	@Override
	public Result<Node, CompileError> lex(String content) {
		return rule.lex(content).map(node -> node.retype(type));
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		if (node.is(type)) return rule.generate(node);
		else return new Err<>(new CompileError("Type '" + type + "' not present", new NodeContext(node)));
	}
}
