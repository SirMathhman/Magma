package magma.rule;

import magma.node.Node;

import java.util.Optional;

public record PrefixRule(String prefix, Rule rule) implements Rule {
	@Override
	public Optional<Node> lex(final String input) {
		if (input.startsWith(this.prefix)) return this.rule.lex(input.substring(this.prefix.length()));
		return Optional.empty();
	}

	@Override
	public Optional<String> generate(final Node node) {
		return this.rule.generate(node).map(result -> this.prefix + result);
	}
}
