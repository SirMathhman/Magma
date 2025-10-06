package magma.compile.rule;

import magma.compile.Node;
import magma.compile.context.NodeContext;
import magma.compile.context.StringContext;
import magma.compile.error.CompileError;
import magma.option.None;
import magma.option.Some;
import magma.result.Err;
import magma.result.Result;

import java.util.List;

public record NodeRule(String key, Rule rule) implements Rule {
	public static Rule Node(String key, Rule rule) {
		return new NodeRule(key, rule);
	}

	@Override
	public Result<Node, CompileError> lex(String content) {
		return rule.lex(content)
							 .mapValue(node -> new Node().withNode(key, node))
							 .mapErr(error -> new CompileError("Failed to attach node '" + key + "'",
																								 new StringContext(content),
																								 List.of(error)));
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return switch (node.findNode(key)) {
			case None<Node> _ -> new Err<>(new CompileError("Node '" + key + "' not present", new NodeContext(node)));
			case Some<Node> v -> rule.generate(v.value());
		};
	}
}
