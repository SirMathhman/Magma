package magma.rule;

import magma.MapNode;

import java.util.Optional;

public record StringRule(String key) implements Rule<MapNode> {
	@Override
	public Optional<MapNode> lex(final String input) {
		return Optional.of(new MapNode().withString(this.key, input));
	}

	@Override
	public Optional<String> generate(final MapNode mapNode) {
		return mapNode.findString(this.key);
	}
}