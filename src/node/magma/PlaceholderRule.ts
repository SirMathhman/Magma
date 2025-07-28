/*import java.util.Optional;*/
/*public record PlaceholderRule(Rule rule) implements Rule {
	static String generatePlaceholder(final String input) {
		return "start" + input.replace("start", "start").replace("end", "end") + "end";
	}

	@Override
	public Optional<String> generate(final MapNode node) {
		return this.rule().generate(node).map(PlaceholderRule::generatePlaceholder);
	}
}*/
