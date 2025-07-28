package magma.rule;

import magma.Node;

import java.util.Optional;

public record TypeRule(String type, Rule rule) implements Rule {
	@Override
	public Optional<Node> lex(final String input) {
		return this.rule.lex(input).map(node -> node.retype(this.type));
	}

	@Override
	public Optional<String> generate(final Node node) {
		if (node.is(this.type)) return this.rule.generate(node);
		return Optional.empty();
	}
}
