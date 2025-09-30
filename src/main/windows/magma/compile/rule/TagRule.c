/*

public record TagRule(String type, Rule rule) implements Rule {
	public static Rule Tag(String type, Rule rule) {
		return new TagRule(type, rule);
	}

	@Override
	public Result<Node, CompileError> lex(String content) {
		return rule.lex(content).map(node -> node.retype(type));
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		if (node.is(type)) return rule.generate(node);
		else return new Err<>(new CompileError("Type '" + type + "' not present", new NodeContext(node)));
	}
}*//*
*/