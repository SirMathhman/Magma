package magma.rule;

import magma.MapNode;
import magma.Node;

import java.util.Optional;

public record StringRule(String key) implements Rule {
	@Override
	public Optional<Node> lex(final String input) {
		return Optional.of(new MapNode().withString(this.key, input));
	}

	@Override
	public Optional<String> generate(final Node node) {
		return node.findString(this.key);
	}
}