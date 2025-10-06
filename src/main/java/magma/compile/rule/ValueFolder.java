package magma.compile.rule;

import magma.option.Some;

public record ValueFolder() implements Folder {
	@Override
	public DivideState fold(DivideState state, char c) {
		if (c == ',' && state.isLevel()) return state.advance();

		DivideState appended = state.append(c);
		if (c == '-') if (appended.peek() instanceof Some<Character>(Character next) && next == '>')
			return appended.popAndAppendToOption().orElse(appended);

		if (c == '<' || c == '(') return appended.enter();
		if (c == '>' || c == ')') return appended.exit();
		return appended;
	}

	@Override
	public String delimiter() {
		return ", ";
	}
}
