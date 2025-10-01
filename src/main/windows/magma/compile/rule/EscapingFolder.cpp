struct EscapingFolder<>{};
DivideState fold_EscapingFolder(DivideState state, char c) {/*
		if (c == '\'') return state.append(c)
															 .popAndAppendToTuple()
															 .map(this::foldEscape)
															 .flatMap(DivideState::popAndAppendToOption)
															 .orElse(state);

		return folder.fold(state, c);
	*/}
char* delimiter_EscapingFolder() {/*
		return folder.delimiter();
	*/}
DivideState foldEscape_EscapingFolder(Character tuple) {/*
		if (tuple.right() == '\\') return tuple.left().popAndAppendToOption().orElse(tuple.left());
		else return tuple.left();
	*/}
