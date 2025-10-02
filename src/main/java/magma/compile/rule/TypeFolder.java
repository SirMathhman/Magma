package magma.compile.rule;

/**
 * Folder for handling Java type syntax including generics.
 * Understands angle brackets for generics (e.g., Function&lt;T, R&gt;)
 * and parentheses for method parameters, splitting on spaces at depth 0.
 */
public record TypeFolder() implements Folder {
	@Override
	public DivideState fold(DivideState state, char c) {
		// Split on space when at depth 0
		if (c == ' ' && state.isLevel()) return state.advance();

		// Track depth for angle brackets (generics)
		final DivideState append = state.append(c); if (c == '<') return append.enter(); if (c == '>') return append.exit();

		// Track depth for parentheses (method params, etc.)
		if (c == '(') return append.enter(); if (c == ')') return append.exit();

		// Append everything else
		return append;
	}

	@Override
	public String delimiter() {
		return " ";
	}
}
