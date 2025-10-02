package magma.compile.rule;

public class BraceStartFolder implements Folder {
	@Override
	public DivideState fold(DivideState state, char c) {
		if (c == '{') {
			final DivideState entered = state.enter();
			if (entered.isShallow()) return entered.advance();
			return entered.append(c);
		}

		final DivideState state1 = state.append(c);
		if (c == '(') return state1.enter();
		if (c == '}' || c == ')') return state1.exit();
		return state1;
	}

	@Override
	public String delimiter() {
		return "{";
	}
}
