package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.NodeContext;
import magma.compile.error.CompileError;
import magma.result.Err;
import magma.result.Result;

public record NodeRule(String key, Rule rule) implements Rule {
	public static Rule Node(String key, Rule rule) {
		return new NodeRule(key, rule);
	}

	@Override
	public Result<Node, CompileError> lex(String content) {
		return rule.lex(content).map(node -> new Node().withNode(key, node));
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return new Err<>(new CompileError("Cannot generate for node group '" + key + "'", new NodeContext(node)));
	}
}
