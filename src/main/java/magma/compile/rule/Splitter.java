package magma.compile.rule;

import magma.option.Option;
import magma.Tuple;

public interface Splitter {
	/**
	 * Split the input string into left and right parts.
	 * Returns None if splitting is not possible.
	 * Returns Some(Tuple(left, right)) if splitting succeeds.
	 */
	Option<Tuple<String, String>> split(String input);
}
