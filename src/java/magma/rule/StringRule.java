package magma.rule;

import magma.node.Node;

import java.util.Optional;

public record StringRule(String key) implements Rule {
	@Override
	public Optional<String> generate(final Node node) {
		return node.findString(this.key());
	}
}