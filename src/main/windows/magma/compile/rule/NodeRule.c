/*

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
		return switch (node.findNode(key)) {
			case None<Node> _ -> new Err<>(new CompileError("Node '" + key + "' not present", new NodeContext(node)));
			case Some<Node> v -> rule.generate(v.value());
		};
	}
}*//*
*/