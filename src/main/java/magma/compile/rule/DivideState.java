package magma.compile.rule;

import magma.Tuple;
import magma.list.ArrayList;
import magma.list.List;
import magma.list.Stream;
import magma.option.Option;
import magma.option.Some;

public class DivideState {
	public final List<TokenSequence> segments = new ArrayList<TokenSequence>();
	private final TokenSequence input;
	private StringBuilder buffer = new StringBuilder();
	private int depth = 0;
	private int index;

	public DivideState(TokenSequence input) {
		this.input = input;
	}

	Stream<TokenSequence> stream() {
		return segments.stream();
	}

	public DivideState enter() {
		this.depth = depth + 1;
		return this;
	}

	public DivideState advance() {
		segments.addLast(new StringTokenSequence(buffer.toString()));
		this.buffer = new StringBuilder();
		return this;
	}

	public DivideState append(char c) {
		buffer.append(c);
		return this;
	}

	public DivideState exit() {
		this.depth = depth - 1;
		return this;
	}

	public boolean isShallow() {
		return depth == 1;
	}

	public boolean isLevel() {
		return depth == 0;
	}

	public Option<Character> pop() {
		final Option<Character> maybeNext = input.charAt(index);
		if (maybeNext instanceof Some<Character>) index++;
		return maybeNext;
	}

	public Option<Tuple<DivideState, Character>> popAndAppendToTuple() {
		return pop().map(popped -> new Tuple<DivideState, Character>(append(popped), popped));
	}

	public Option<DivideState> popAndAppendToOption() {
		return popAndAppendToTuple().map(Tuple::left);
	}

	public Option<Character> peek() {
		return input.charAt(index);
	}
}
