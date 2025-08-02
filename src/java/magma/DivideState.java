package magma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

final class DivideState {
	private final StringBuilder buffer = new StringBuilder();
	private final Collection<String> segments = new ArrayList<>();
	private final CharSequence input;
	private int depth = 0;
	private int index = 0;

	DivideState(final CharSequence input) {
		this.input = input;
	}

	boolean hasNextChar(final char c) {
		if (this.index < this.input.length()) return this.input.charAt(this.index) == c;
		else return false;
	}

	Stream<String> stream() {
		return this.segments.stream();
	}

	DivideState append(final char c) {
		this.buffer.append(c);
		return this;
	}

	DivideState enter() {
		this.depth = this.depth + 1;
		return this;
	}

	boolean isLevel() {
		return 0 == this.depth;
	}

	DivideState advance() {
		this.segments.add(this.buffer.toString());
		this.buffer.setLength(0);
		return this;
	}

	DivideState exit() {
		this.depth = this.depth - 1;
		return this;
	}

	boolean isShallow() {
		return 1 == this.depth;
	}

	Option<Tuple<DivideState, Character>> pop() {
		if (this.index >= this.input.length()) return new None<>();
		final var next = this.input.charAt(this.index);
		this.index++;
		return new Some<>(new Tuple<>(this, next));
	}

	Option<Tuple<DivideState, Character>> popAndAppendToTuple() {
		return this.pop().map(tuple -> new Tuple<>(tuple.left().append(tuple.right()), tuple.right()));
	}

	Option<DivideState> popAndAppendToOption() {
		return this.popAndAppendToTuple().map(Tuple::left);
	}
}
