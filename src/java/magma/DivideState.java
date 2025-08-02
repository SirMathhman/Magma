package magma;

import magma.option.None;
import magma.option.Option;
import magma.option.Some;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * A state machine for parsing and tokenizing input text.
 * 
 * DivideState maintains the current state during parsing operations, including:
 * <ul>
 *   <li>A buffer for accumulating characters</li>
 *   <li>A collection of parsed segments</li>
 *   <li>The input text being processed</li>
 *   <li>A depth counter for tracking nested structures</li>
 *   <li>The current position in the input</li>
 * </ul>
 * 
 * This class is used extensively in the parsing and compilation process to
 * transform input text into structured segments that can be further processed.
 */
final class DivideState {
	/** Buffer for accumulating characters during parsing */
	private final StringBuilder buffer = new StringBuilder();
	
	/** Collection of completed segments */
	private final Collection<String> segments = new ArrayList<>();
	
	/** The input text being processed */
	private final CharSequence input;
	
	/** Current nesting depth (for tracking nested structures) */
	private int depth = 0;
	
	/** Current position in the input */
	private int index = 0;

	/**
	 * Creates a new DivideState with the specified input.
	 *
	 * @param input The input text to be processed
	 */
	DivideState(final CharSequence input) {
		this.input = input;
	}

	/**
	 * Checks if the next character in the input matches the specified character.
	 *
	 * @param c The character to check for
	 * @return true if the next character matches, false otherwise
	 */
	boolean hasNextChar(final char c) {
		if (this.index < this.input.length()) return this.input.charAt(this.index) == c;
		else return false;
	}

	/**
	 * Returns a stream of all completed segments.
	 *
	 * @return A stream containing all segments that have been processed
	 */
	Stream<String> stream() {
		return this.segments.stream();
	}

	/**
	 * Appends a character to the current buffer.
	 *
	 * @param c The character to append
	 * @return This DivideState instance for method chaining
	 */
	DivideState append(final char c) {
		this.buffer.append(c);
		return this;
	}

	/**
	 * Increases the nesting depth by 1.
	 * Used when entering nested structures like parentheses, braces, etc.
	 *
	 * @return This DivideState instance for method chaining
	 */
	DivideState enter() {
		this.depth = this.depth + 1;
		return this;
	}

	/**
	 * Checks if the current depth is at the top level (0).
	 *
	 * @return true if at the top level, false otherwise
	 */
	boolean isLevel() {
		return 0 == this.depth;
	}

	/**
	 * Completes the current segment by adding the buffer contents to the segments collection
	 * and clearing the buffer.
	 *
	 * @return This DivideState instance for method chaining
	 */
	DivideState advance() {
		this.segments.add(this.buffer.toString());
		this.buffer.setLength(0);
		return this;
	}

	/**
	 * Decreases the nesting depth by 1.
	 * Used when exiting nested structures like parentheses, braces, etc.
	 *
	 * @return This DivideState instance for method chaining
	 */
	DivideState exit() {
		this.depth = this.depth - 1;
		return this;
	}

	/**
	 * Checks if the current depth is at the first level of nesting (1).
	 *
	 * @return true if at the first level of nesting, false otherwise
	 */
	boolean isShallow() {
		return 1 == this.depth;
	}

	/**
	 * Retrieves the next character from the input and advances the position.
	 * Returns an Option containing a Tuple of this DivideState and the next character,
	 * or None if the end of input has been reached.
	 *
	 * @return An Option containing a Tuple of this DivideState and the next character, or None
	 */
	Option<Tuple<DivideState, Character>> pop() {
		if (this.index >= this.input.length()) return new None<>();
		final var next = this.input.charAt(this.index);
		this.index++;
		return new Some<>(new Tuple<>(this, next));
	}

	/**
	 * Retrieves the next character, appends it to the buffer, and returns a Tuple
	 * containing the updated state and the character.
	 *
	 * @return An Option containing a Tuple of the updated DivideState and the next character, or None
	 */
	Option<Tuple<DivideState, Character>> popAndAppendToTuple() {
		return this.pop().map(tuple -> new Tuple<>(tuple.left().append(tuple.right()), tuple.right()));
	}

	/**
	 * Retrieves the next character, appends it to the buffer, and returns the updated state.
	 *
	 * @return An Option containing the updated DivideState, or None
	 */
	Option<DivideState> popAndAppendToOption() {
		return this.popAndAppendToTuple().map(Tuple::left);
	}
}
