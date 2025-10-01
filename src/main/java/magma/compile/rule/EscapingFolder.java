package magma.compile.rule;

import magma.Tuple;

public record EscapingFolder(Folder folder) implements Folder {
	@Override
	public DivideState fold(DivideState state, char c) {
		if (c == '\'') return state.append(c)
															 .popAndAppendToTuple()
															 .map(this::foldEscape)
															 .flatMap(DivideState::popAndAppendToOption)
															 .orElse(state);

		return state;
	}

	private DivideState foldEscape(Tuple<DivideState, Character> tuple) {
		if (tuple.right() == '\\') return tuple.left().popAndAppendToOption().orElse(tuple.left());
		else return tuple.left();
	}
}
