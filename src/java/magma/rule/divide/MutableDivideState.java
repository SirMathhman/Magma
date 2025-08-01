package magma.rule.divide;

import magma.Tuple;
import magma.input.Input;
import magma.input.RootInput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A mutable implementation of the DivideState interface that tracks state
 * while dividing input text into segments.
 * <p>
 * This class maintains a buffer, a collection of segments, and state information
 * such as the current index and nesting depth. It's used by NodeListRule to
 * parse structured text with nested blocks and segment boundaries.
 * <p>
 * The state transitions (enter, exit, advance) modify the internal state directly,
 * allowing for efficient parsing of the input text.
 */
public class MutableDivideState implements DivideState {
	private final Input input;
	private final Collection<Input> segments;
	private Input buffer;
	private int index;
	private int depth;

	/**
	 * Creates a new MutableDivideState with the given input.
	 *
	 * @param input the input to divide
	 */
	MutableDivideState(final Input input) {
		this.input = input;
		this.index = 0; this.depth = 0;
		this.segments = new ArrayList<>();
		this.buffer = new RootInput("", input.getSource() + " (buffer)", input.getStartIndex(), input.getStartIndex());
	}

	/**
	 * Creates a new MutableDivideState with the given string input.
	 * This constructor is provided for backward compatibility.
	 *
	 * @param input the string input to divide
	 */
	MutableDivideState(final String input) {
		this(new RootInput(input));
	}

	@Override
	public final Optional<Tuple<DivideState, Character>> pop() {
		final var length = this.input.getContent().length();
		if (this.index >= length) return Optional.empty(); final char c = this.input.getContent().charAt(this.index);

		this.index = this.index + 1;
		return Optional.of(new Tuple<>(this, c));
	}

	@Override
	public final boolean isLevel() {
		return 0 == this.depth;
	}

	@Override
	public final DivideState exit() {
		this.depth = this.depth - 1;
		return this;
	}

	@Override
	public final DivideState enter() {
		this.depth = this.depth + 1;
		return this;
	}

	@Override
	public final DivideState advance() {
		this.segments.add(this.buffer);
		// Reset buffer to empty with updated start position
		int newStartPosition = this.input.getStartIndex() + this.index;
		this.buffer = new RootInput("", this.input.getSource() + " (buffer)", newStartPosition, newStartPosition);
		return this;
	}

	@Override
	public final DivideState append(final char c) {
		// Extend the buffer with the new character
		this.buffer = this.buffer.extendEnd(String.valueOf(c));
		return this;
	}

	@Override
	public final Stream<Input> stream() {
		return this.segments.stream();
	}

	@Override
	public final boolean isShallow() {
		return 0 == this.depth;
	}
}
