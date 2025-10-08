package magma.compile.rule;

import magma.Tuple;
import magma.option.Option;

public interface Splitter {
	/**
	 * Split the input string into left and right parts.
	 * Returns None if splitting is not possible.
	 * Returns Some(Tuple(left, right)) if splitting succeeds.
	 */
	Option<Tuple<TokenSequence, TokenSequence>> split(TokenSequence tokenSequence);

	String createErrorMessage();

	String merge(String left, String right);
}
