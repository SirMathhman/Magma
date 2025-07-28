package magma.rule;

import magma.MapNode;

import java.util.Optional;

public record SuffixRule(Rule<MapNode> rule, String suffix) implements Rule<MapNode> {
	@Override
	public Optional<MapNode> lex(final String input) {
		if (input.endsWith(this.suffix)) {
			final var slice = input.substring(0, input.length() - this.suffix.length());
			return this.rule.lex(slice);
		} else return Optional.empty();
	}

	@Override
	public Optional<String> generate(final MapNode mapNode) {
		return this.rule.generate(mapNode).map(result -> result + this.suffix);
	}
}