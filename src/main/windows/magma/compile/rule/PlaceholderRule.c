/*

public record PlaceholderRule(Rule rule) implements Rule {
	private static String wrap(String input) {
		return "start" + input.replace("start", "start").replace("end", "end") + "end";
	}

	public static Rule Placeholder(Rule rule) {
		return new PlaceholderRule(rule);
	}

	@Override
	public Result<Node, CompileError> lex(String content) {
		return rule.lex(content);
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return rule().generate(node).map(PlaceholderRule::wrap);
	}
}*//*
*/