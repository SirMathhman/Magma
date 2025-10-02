package magma.compile.rule;

import magma.Tuple;
import magma.option.Some;

public record EscapingFolder(Folder folder) implements Folder {
	@Override
	public DivideState fold(DivideState state, char c) {
		if (c == '\'') return state.append(c)
															 .popAndAppendToTuple()
															 .map(this::foldEscape)
															 .flatMap(DivideState::popAndAppendToOption)
															 .orElse(state);

		// handle comments
		if (c == '/' && state.isLevel()) {
			final DivideState withSlash = state.append(c);
			if (withSlash.peek() instanceof Some<Character>(Character next) && next == '/') {
				DivideState current = withSlash.popAndAppendToOption().orElse(state); while (true) {
					if (current.popAndAppendToTuple() instanceof Some<Tuple<DivideState, Character>>(
							Tuple<DivideState, Character> tuple
					)) {
						current = tuple.left(); if (tuple.right() == '\n') {
							current = current.advance(); break;
						}
					} else break;
				}

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
