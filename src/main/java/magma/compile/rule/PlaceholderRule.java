package magma.compile.rule;

import magma.compile.Node;
import magma.compile.error.CompileError;
import magma.result.Result;

public record PlaceholderRule(Rule rule) implements Rule {
	private static String wrap(String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
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
		return rule.generate(node).mapValue(PlaceholderRule::wrap);
	}
}
