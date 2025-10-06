package magma.compile.rule;

import magma.Tuple;
import magma.option.Option;

public record EscapingFolder(Folder folder) implements Folder {
	@Override
	public DivideState fold(DivideState state, char c) {
		return handleSingleQuotes(state, c).or(() -> handleDoubleQuotes(state, c))
																			 .or(() -> handleComments(state, c))
																			 .orElseGet(() -> folder.fold(state, c));
	}

	private Option<DivideState> handleSingleQuotes(DivideState state, char c) {
		if (c != '\'') return Option.empty();
		return Option.of(state.append(c)
													.popAndAppendToTuple()
													.map(this::foldEscape)
													.flatMap(DivideState::popAndAppendToOption)
													.orElse(state));
	}

	private Option<DivideState> handleDoubleQuotes(DivideState state, char c) {
		if (c != '\"') return Option.empty();
		DivideState current = state.append(c);
		while (true) {
			final Option<Tuple<DivideState, Character>> tupleOption = current.popAndAppendToTuple();
			if (tupleOption instanceof Option.Some<Tuple<DivideState, Character>>(Tuple<DivideState, Character> t0)) {
				current = t0.left();

				if (t0.right() == '\\') current = current.popAndAppendToOption().orElse(current);
				if (t0.right() == '\"') break;
			} else break;
		}
		return Option.of(current);
	}

	private Option<DivideState> handleComments(DivideState state, char c) {
		// handle comments
		if (c == '/' && state.isLevel()) return handleLineComments(state).or(() -> handleBlockComments(state, c));
		return Option.empty();
	}

	private Option<DivideState> handleLineComments(DivideState state) {
		if (!(state.peek() instanceof Option.Some<Character>(Character next)) || next != '/') return Option.empty();
		while (true) {
			Option<Character> pop = state.pop();
			if (pop instanceof Option.None || (pop instanceof Option.Some<Character>(Character c) && c == '\n')) return Option.of(state);
		}
	}

	private Option<DivideState> handleBlockComments(DivideState state, char c) {
		if (!(state.peek() instanceof Option.Some<Character>(Character next)) || next != '*') return Option.empty();

		final DivideState withSlash = state.append(c);
		Tuple<Boolean, DivideState> current = new Tuple<>(true, withSlash.popAndAppendToOption().orElse(state));
		while (current.left()) current = handle(current.right());
		return new Option.Some<>(current.right());
	}

	private Tuple<Boolean, DivideState> handle(DivideState current) {
		if (!(current.popAndAppendToTuple() instanceof Option.Some<Tuple<DivideState, Character>>(
				Tuple<DivideState, Character> tuple
		))) return new Tuple<>(false, current);

		DivideState temp = tuple.left();
		if (tuple.right() == '*' && temp.peek() instanceof Option.Some<Character>(Character tuple0) && tuple0 == '/')
			return new Tuple<>(false, temp.popAndAppendToOption().orElse(temp).advance());
		return new Tuple<>(true, temp);
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
