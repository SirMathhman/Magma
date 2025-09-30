package magma.compile.rule;

import magma.compile.Node;
import magma.compile.error.CompileError;
import magma.result.Result;

public record StripRule(Rule rule) implements Rule {
	public static Rule Strip(Rule rule) {
		return new StripRule(rule);
	}

	@Override
	public Result<Node, CompileError> lex(String content) {
		return rule.lex(content.strip());
	}

	@Override
	public Result<String, CompileError> generate(Node node) {
		return rule.generate(node);
	}
}
