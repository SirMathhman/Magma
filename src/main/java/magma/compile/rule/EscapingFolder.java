package magma.compile.rule;

import magma.Tuple;
import magma.option.Option;
import magma.option.Some;

public record EscapingFolder(Folder folder) implements Folder {
	@Override
	public DivideState fold(DivideState state, char c) {
		if (c == '\'') return state.append(c)
															 .popAndAppendToTuple()
															 .map(this::foldEscape)
															 .flatMap(DivideState::popAndAppendToOption)
															 .orElse(state);

		if (c == '\"') {
			DivideState current = state.append(c);
			while (true) {
				final Option<Tuple<DivideState, Character>> tupleOption = current.popAndAppendToTuple();
				if (tupleOption instanceof Some<Tuple<DivideState, Character>>(Tuple<DivideState, Character> t0)) {
					current = t0.left();

					if (t0.right() == '\\') current = current.popAndAppendToOption().orElse(current);
					if (t0.right() == '\"') break;
				} else break;
			}
			return current;
		}

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

			// Handle block comments /* */
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
	}

	@Override
	public String delimiter() {
		return folder.delimiter();
	}

	private DivideState foldEscape(Tuple<DivideState, Character> tuple) {
		if (tuple.right() == '\\') return tuple.left().popAndAppendToOption().orElse(tuple.left());
		else return tuple.left();
	}
}
