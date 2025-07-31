package magma.rule;

import magma.node.Node;

import java.util.Optional;

public final class PlaceholderRule implements Rule {
	private final Rule rule;

	public PlaceholderRule(final Rule rule) {
		this.rule = rule;
	}

	@Override
	public Optional<String> generate(final Node node) {
		return this.rule.generate(node).map(content -> "/*" + content + "*/");
	}
	
	@Override
	public Optional<Node> lex(final String input) {
		if (!input.startsWith("/*") || !input.endsWith("*/")) {
			return Optional.empty();
		}
		
		// Extract content between /* and */
		String content = input.substring(2, input.length() - 2);
		return this.rule.lex(content);
	}
}