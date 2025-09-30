package magma.compile.rule;

import magma.compile.Node;
import magma.compile.error.CompileError;
import magma.result.Result;

public record PlaceholderRule(Rule rule) implements Rule {
	private static String wrap(String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
	}

	@Override
	public Result<Node, CompileError> lex(String content) {
		return rule.lex(content);
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return rule().generate(node).map(PlaceholderRule::wrap);
	}
}
