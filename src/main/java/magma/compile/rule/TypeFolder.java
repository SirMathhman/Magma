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
		if (c == ' ' && state.isLevel()) {
			return state.advance();
		}

		// Track depth for angle brackets (generics)
		if (c == '<') {
			return state.enter().append(c);
		}
		if (c == '>') {
			return state.exit().append(c);
		}

		// Track depth for parentheses (method params, etc.)
		if (c == '(') {
			return state.enter().append(c);
		}
		if (c == ')') {
			return state.exit().append(c);
		}

		// Append everything else
		return state.append(c);
	}

	@Override
	public String delimiter() {
		return " ";
	}
}
