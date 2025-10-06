package magma.compile.rule;

import magma.option.Option;

public class StatementFolder implements Folder {
	@Override
	public DivideState fold(DivideState state, char c) {
		final DivideState appended = state.append(c);
		if (c == '-') {
			if (appended.peek() instanceof Option.Some<Character>(Character next) && next == '>') {
				return state.popAndAppendToOption().orElse(state);
			}
		}

		if (c == ';' && appended.isLevel()) return appended.advance();
		if (c == '}' && appended.isShallow()) {
			if (appended.peek() instanceof Option.Some<Character>(Character next) && next == ';')
				return appended.popAndAppendToOption().orElse(appended).advance().exit();
			return appended.advance().exit();
		}
		if (c == '{' || c == '(') return appended.enter();
		if (c == '}' || c == ')') return appended.exit();
		return appended;
	}

	@Override
	public String delimiter() {
		return "";
	}
}