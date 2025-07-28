/*import magma.node.Node;*/
/*import java.util.Optional;*/
/*public record InfixRule(Rule leftRule, String infix, Rule rightRule) implements Rule {

	@Override
	public Optional<Node> lex(final String input) {
		final String infix1 = this.infix();
		final var index = input.indexOf(infix1);
		if (0 > index) return Optional.empty();
		final var left = input.substring(0, index).strip();
		final var right = input.substring(index + infix1.length());
		return this.leftRule.lex(left).flatMap(name -> {
			return this.rightRule.lex(right).map(name::merge);
		});
	}

	@Override
	public Optional<String> generate(final Node node) {
		return this.leftRule().generate(node).flatMap(leftResult -> {
			return this.rightRule().generate(node).map(rightResult -> leftResult + this.infix() + rightResult);
		});
	}
}*/
