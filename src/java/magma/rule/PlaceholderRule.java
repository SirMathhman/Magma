package magma.rule;

import magma.MapNode;

import java.util.Optional;

public record PlaceholderRule(Rule<MapNode> rule) implements Rule<MapNode> {
	public static String wrap(final String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
	}

	@Override
	public Optional<MapNode> lex(final String input) {
		return this.rule.lex(input);
	}

	@Override
	public Optional<String> generate(final MapNode node) {
		return this.rule().generate(node).map(PlaceholderRule::wrap);
	}
}