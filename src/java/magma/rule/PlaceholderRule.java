package magma.rule;

import magma.node.Node;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

public final class PlaceholderRule implements Rule {
	private final Rule rule;

	public PlaceholderRule(final Rule rule) {
		this.rule = rule;
	}

	@Override
	public Result<String, String> generate(final Node node) {
		Result<String, String> result = this.rule.generate(node); if (result.isErr()) {
			return result;
		} return new Ok<>("/*" + result.unwrap() + "*/");
	}
	
	@Override
	public Result<Node, String> lex(final String input) {
		if (!input.startsWith("/*") || !input.endsWith("*/")) {
			return new Err<>("Input is not a placeholder (must start with /* and end with */)");
		}
		
		// Extract content between /* and */
		String content = input.substring(2, input.length() - 2);
		return this.rule.lex(content);
	}
}