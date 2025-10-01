struct StatementFolder<>{};
DivideState fold_StatementFolder(DivideState state, char c) {/*
		final DivideState appended = state.append(c); if (c == ';' && appended.isLevel()) return appended.advance();
		if (c == '}' && appended.isShallow()) return appended.advance().exit(); if (c == '{') return appended.enter();
		if (c == '}') return appended.exit(); return appended;
	*/}
char* delimiter_StatementFolder() {/*
		return "";
	*/}
