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
}