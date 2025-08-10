package magma;

import java.util.Optional;

public record SuffixRule(StringRule rule, String suffix) {
	Optional<MapNode> lex(String input) {
		if (!input.endsWith(suffix())) return Optional.empty();
		final var content = input.substring(0, input.length() - suffix().length());
		return rule().lex(content);
	}
}