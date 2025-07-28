package magma.rule;

import magma.MapNode;

import java.util.Optional;

public record PlaceholderRule(Rule rule) implements Rule {
	public static String wrap(final String input) {
		return "/*" + input.replace("/*", "start").replace("*/", "end") + "*/";
	}

	@Override
	public Optional<String> generate(final MapNode node) {
		return this.rule().generate(node).map(PlaceholderRule::wrap);
	}
}