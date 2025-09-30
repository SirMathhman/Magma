package magma.compile.rule;

import java.util.stream.Stream;

public class StatementDivider implements Divider {
	@Override
	public Stream<String> divide(String input) {
		DivideState current = new DivideState(); for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i); current = fold(current, c);
		}

		return current.advance().stream();
	}

	private DivideState fold(DivideState state, char c) {
		final DivideState appended = state.append(c); if (c == ';' && appended.isLevel()) return appended.advance();
		if (c == '}' && appended.isShallow()) return appended.advance().exit(); if (c == '{') return appended.enter();
		if (c == '}') return appended.exit(); return appended;
	}
}
