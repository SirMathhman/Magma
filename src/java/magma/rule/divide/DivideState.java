package magma.rule.divide;

import magma.Tuple;
import magma.input.Input;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Interface defining a state machine for dividing input text into segments.
 * <p>
 * This interface provides operations for parsing structured text with nested blocks
 * and segment boundaries. It's used by NodeListRule to break input into segments
 * that can be individually lexed.
 * <p>
 * The state machine tracks nesting depth and segment boundaries, allowing for
 * proper handling of nested structures like braces and other delimiters.
 */
public interface DivideState {
	Optional<Tuple<DivideState, Character>> pop();

	boolean isLevel();

	DivideState exit();

	DivideState enter();

	DivideState advance();

	DivideState append(char c);

	Stream<Input> stream();

	boolean isShallow();
}
