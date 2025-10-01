struct EscapingFolder(Folder folder) implements Folder{};
DivideState fold_EscapingFolder(Folder folder) implements Folder(DivideState state, char c) {/*
		if (c == '\'') return state.append(c)
															 .popAndAppendToTuple()
															 .map(this::foldEscape)
															 .flatMap(DivideState::popAndAppendToOption)
															 .orElse(state);

		return folder.fold(state, c);
	*/}
char* delimiter_EscapingFolder(Folder folder) implements Folder() {/*
		return folder.delimiter();
	*/}
DivideState foldEscape_EscapingFolder(Folder folder) implements Folder(Character tuple) {/*
		if (tuple.right() == '\\') return tuple.left().popAndAppendToOption().orElse(tuple.left());
		else return tuple.left();
	*/}
