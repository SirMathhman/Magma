package magma.compile.rule;

import magma.list.List;
import magma.option.Option;

/*
Should be functionally equivalent to List<Token>.
*/
public interface TokenSequence {
	// We should replace this usage with some sort of getFirst() -> Option<Token> and then assert whether Token has that string.
	boolean equalsSlice(String value);

	boolean isEmpty();

	// String should become Token here.
	boolean startsWith(String slice);

	TokenSequence substring(int extent);

	TokenSequence strip();

	// String should become Token here.
	boolean endsWith(String slice);

	int length();

	TokenSequence substring(int start, int end);

	// String should become Token here eventually.
	Option<Integer> indexOf(String infix);

	// This will be removed into a proper division operation at some point.
	List<TokenSequence> split(String regex);

	// We will eventually remove this because this is nonsensical in a list of tokens.
	Option<Character> charAt(int index);

	// String should become Token here eventually.
	Option<Integer> lastIndexOf(String infix);

	// Remove, because we shouldn't have a notion of "String".
	@Deprecated
	String value();

	String display();

	// Functionally equivalent to wrapping String in a TokenSequence and calling appendSequence.
	TokenSequence appendSlice(String delimiter);

	TokenSequence appendSequence(TokenSequence sequence);
}
