package magma.rule;

import magma.node.Node;

import java.util.Optional;

public record SuffixRule(Rule rule, String suffix) implements Rule {
	@Override
	public Optional<Node> lex(final String input) {
		if (input.endsWith(this.suffix)) {
			final var slice = input.substring(0, input.length() - this.suffix.length());
			return this.rule.lex(slice);
		} else return Optional.empty();
	}

	@Override
	public Optional<String> generate(final Node node) {
		return this.rule.generate(node).map(result -> result + this.suffix);
	}
}