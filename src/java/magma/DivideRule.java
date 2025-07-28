package magma;

import magma.node.MapNode;
import magma.node.Node;
import magma.rule.Rule;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public record DivideRule(String key, Rule rule) implements Rule {
	private static State divide(final CharSequence input) {
		final var length = input.length();
		var current = new State();
		for (var i = 0; i < length; i++) {
			final var c = input.charAt(i);
			current = DivideRule.fold(current, c);
		}

		return current.advance();
	}

	private static State fold(final State state, final char c) {
		final var appended = state.append(c);
		if (';' == c && appended.isLevel()) return appended.advance();
		if ('{' == c) return appended.enter();
		if ('}' == c) return appended.exit();
		return appended;
	}

	@Override
	public Optional<Node> lex(final String input) {
		final var children = DivideRule.divide(input).stream().map(this.rule::lex).flatMap(Optional::stream).toList();

		return Optional.of(new MapNode().withNodeList(this.key, children));
	}

	@Override
	public Optional<String> generate(final Node node) {
		return Optional.of(node.findNodeList(this.key)
													 .orElse(Collections.emptyList())
													 .stream()
													 .map(this.rule()::generate)
													 .flatMap(Optional::stream)
													 .collect(Collectors.joining()));
	}
}