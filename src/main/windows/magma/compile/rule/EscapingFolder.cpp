// Generated transpiled C++ from 'src\main\java\magma\compile\rule\EscapingFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<>
struct EscapingFolder{Folder folder;};
template<>
DivideState fold_EscapingFolder(DivideState state, char c) {/*
		if (c == '\'') return state.append(c)
															 .popAndAppendToTuple()
															 .map(this::foldEscape)
															 .flatMap(DivideState::popAndAppendToOption)
															 .orElse(state);

		return folder.fold(state, c);
	*/}
template<>
char* delimiter_EscapingFolder() {/*
		return folder.delimiter();
	*/}
template<>
DivideState foldEscape_EscapingFolder(Character tuple) {/*
		if (tuple.right() == '\\') return tuple.left().popAndAppendToOption().orElse(tuple.left());
		else return tuple.left();
	*/}
