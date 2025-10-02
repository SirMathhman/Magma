package magma.compile.rule;

import magma.Tuple;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DivideState {
	public final List<String> segments;
	private final String input;
	private StringBuilder buffer;
	private int depth;
	private int index;

	public DivideState(StringBuilder buffer, int depth, List<String> segments, String input) {
		this.buffer = buffer; this.depth = depth; this.segments = segments; this.input = input;
	}

	public DivideState(String input) {
		this(new StringBuilder(), 0, new ArrayList<>(), input);
	}

	Stream<String> stream() {
		return segments.stream();
	}

	public DivideState enter() {
		this.depth = depth + 1; return this;
	}

	public DivideState advance() {
		segments.add(buffer.toString()); this.buffer = new StringBuilder(); return this;
	}

	public DivideState append(char c) {
		buffer.append(c); return this;
	}

	public DivideState exit() {
		this.depth = depth - 1; return this;
	}

	public boolean isShallow() {
		return depth == 1;
	}

	boolean isLevel() {
		return depth == 0;
	}

	public Option<Character> pop() {
		if (index >= input.length()) return Option.empty(); final char c = input.charAt(index); index++;
		return Option.of(c);
	}

	public Option<Tuple<DivideState, Character>> popAndAppendToTuple() {
		return pop().map(popped -> new Tuple<>(append(popped), popped));
	}

	public Option<DivideState> popAndAppendToOption() {
		return popAndAppendToTuple().map(Tuple::left);
	}

	public Option<Character> peek() {
		if (index < input.length()) return new Some<>(input.charAt(index));
		else return new None<>();
	}
}
