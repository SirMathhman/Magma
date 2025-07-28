/*import magma.MapNode;*/
/*import java.util.Optional;*/
/*public record PlaceholderRule(Rule rule) implements Rule {
	public static String wrap(final String input) {
		return "start" + input.replace("start", "start").replace("end", "end") + "end";
	}

	@Override
	public Optional<MapNode> lex(final String input) {
		return this.rule.lex(input);
	}

	@Override
	public Optional<String> generate(final MapNode node) {
		return this.rule().generate(node).map(PlaceholderRule::wrap);
	}
}*/
