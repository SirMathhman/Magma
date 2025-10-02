// Generated transpiled C++ from 'src\main\java\magma\compile\rule\EscapingFolder.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<>
struct EscapingFolder{Folder folder;};
template<>
/*@Override
	public DivideState*/ fold_EscapingFolder(DivideState state, char c) {/*
		if (c == '\'') return state.append(c)
															 .popAndAppendToTuple()
															 .map(this::foldEscape)
															 .flatMap(DivideState::popAndAppendToOption)
															 .orElse(state);

		// handle comments
		if (c == '/' && state.isLevel()) {
			if (state.peek() instanceof Some<Character>(Character next) && next == '/') {
				final DivideState withSlash = state.append(c);
				DivideState current = withSlash.popAndAppendToOption().orElse(state);
				while (true) if (current.popAndAppendToTuple() instanceof Some<Tuple<DivideState, Character>>(
						Tuple<DivideState, Character> tuple
				)) {
					current = tuple.left();
					if (tuple.right() == '\n') {
						current = current.advance();
						break;
					}
				} else break;

				return current;
			}

			// Handle block comments start end
			if (state.peek() instanceof Some<Character>(Character next) && next == '*') {
				final DivideState withSlash = state.append(c);
				DivideState current = withSlash.popAndAppendToOption().orElse(state);
				while (true) if (current.popAndAppendToTuple() instanceof Some<Tuple<DivideState, Character>>(
						Tuple<DivideState, Character> tuple
				)) {
					current = tuple.left();
					if (tuple.right() == '*') if (current.peek() instanceof Some<Character>(Character tuple0) && tuple0 == '/') {
						current = current.popAndAppendToOption().orElse(current).advance();
						break;
					}
				} else break;

				return current;
			}
		}

		return folder.fold(state, c);
	*/}
template<>
/*@Override
	public String*/ delimiter_EscapingFolder() {/*
		return folder.delimiter();
	*/}
template<>
/*private DivideState*/ foldEscape_EscapingFolder(Tuple<DivideState, Character> tuple) {/*
		if (tuple.right() == '\\') return tuple.left().popAndAppendToOption().orElse(tuple.left());
		else return tuple.left();
	*/}
