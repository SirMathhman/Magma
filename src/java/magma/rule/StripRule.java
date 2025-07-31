package magma.rule;

import magma.error.CompileError;
import magma.node.Node;
import magma.result.Result;

public record StripRule(Rule rule) implements Rule {
	@Override
	public Result<String, CompileError> generate(final Node node) {
		return this.rule.generate(node);
	}

	@Override
	public Result<Node, CompileError> lex(final String input) {
		return this.rule.lex(input.strip());
	}
}
