package magma.rule;

import magma.error.CompileError;
import magma.error.StringContext;
import magma.node.Node;
import magma.result.Err;
import magma.result.Result;

public final class PlaceholderRule implements Rule {
	private final Rule rule;

	public PlaceholderRule(final Rule rule) {
		this.rule = rule;
	}

	@Override
	public Result<String, CompileError> generate(final Node node) {
		final Result<String, CompileError> result = this.rule.generate(node); if (result.isErr()) return result;
		return result.mapValue(value -> "/*" + value + "*/");
	}
	
	@Override
	public Result<Node, CompileError> lex(final String input) {
		if (!input.startsWith("/*") || !input.endsWith("*/")) {
			return new Err<>(new CompileError("Input is not a placeholder (must start with /* and end with */)",
																				new StringContext(input)));
		}
		
		// Extract content between /* and */
		final String content = input.substring(2, input.length() - 2);
		return this.rule.lex(content);
	}
}