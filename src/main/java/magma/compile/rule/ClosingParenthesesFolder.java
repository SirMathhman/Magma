package magma.compile.rule;

public class ClosingParenthesesFolder implements Folder {
	@Override
	public DivideState fold(DivideState state, char c) {
		final DivideState appended = state.append(c); if (c == '(') return appended.enter(); if (c == ')') {
			final DivideState exit = appended.exit(); if (exit.isLevel()) return exit.advance(); return exit;
		} return appended;
	}

	@Override
	public String delimiter() {
		return ")";
	}
}
