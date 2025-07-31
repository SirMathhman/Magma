package magma.rule;

import magma.node.Node;

import java.util.Optional;

final class PlaceholderRule implements Rule {
	private final Rule rule;

	PlaceholderRule(final Rule rule) {
		this.rule = rule;
	}

	@Override
	public Optional<String> generate(final Node node) {
		return this.rule.generate(node).map(content -> "/*" + content + "*/");
	}
}