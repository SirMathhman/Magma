package magma.compile.rule;

public class ClosingParenthesesFolder implements Folder {
	@Override
	public DivideState fold(DivideState state, char c) {
		if (c == '(') return state.append(c).enter();
		if (c == ')') {
			final DivideState exit = state.exit();
			if (exit.isLevel()) return exit.advance();
			return exit.append(c);
		}
		return state.append(c);
	}

	@Override
	public String delimiter() {
		return ")";
	}
}
