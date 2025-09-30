package magma.compile.rule;

public class StatementFolder implements Folder {
	@Override
	public DivideState fold(DivideState state, char c) {
		final DivideState appended = state.append(c); if (c == ';' && appended.isLevel()) return appended.advance();
		if (c == '}' && appended.isShallow()) return appended.advance().exit(); if (c == '{') return appended.enter();
		if (c == '}') return appended.exit(); return appended;
	}
}