/*import magma.node.Node;*/
/*import java.util.List;*/
/*import java.util.Optional;*/
/*public record OrRule(List<Rule> rules) implements Rule {
	@Override
	public Optional<Node> lex(final String input) {
		return this.rules.stream().map(rule -> rule.lex(input)).flatMap(Optional::stream).findFirst();
	}

	@Override
	public Optional<String> generate(final Node node) {
		return this.rules.stream().map(rule -> rule.generate(node)).flatMap(Optional::stream).findFirst();
	}
}*/
