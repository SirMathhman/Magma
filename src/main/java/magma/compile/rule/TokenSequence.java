package magma.compile.rule;

import magma.list.List;
import magma.option.Option;

/*
Should be functionally equivalent to List<Token>.
*/
public interface TokenSequence {
	// ========== Token-based access methods (NEW API) ==========

	/**
	 * Returns the first token in this sequence, if present.
	 *
	 * @return Some(token) if the sequence is non-empty, None otherwise
	 */
	Option<Token> getFirst();

	/**
	 * Returns the last token in this sequence, if present.
	 *
	 * @return Some(token) if the sequence is non-empty, None otherwise
	 */
	Option<Token> getLast();

	/**
	 * Returns the token at the specified index, if present.
	 *
	 * @param index The zero-based index of the token to retrieve
	 * @return Some(token) if index is valid, None otherwise
	 */
	Option<Token> getAt(int index);

	// ========== Legacy string-based methods (TRANSITIONAL) ==========

	// We should replace this usage with some sort of getFirst() -> Option<Token>
	// and then assert whether Token has that string.
	boolean equalsSlice(String value);

	boolean isEmpty();

	// String should become Token here.
	boolean startsWith(String slice);

	/**
	 * Checks if this token sequence starts with the given token.
	 *
	 * @param token The token to check for at the start
	 * @return true if this sequence starts with the given token
	 */
	boolean startsWith(Token token);

	TokenSequence substring(int extent);

	TokenSequence strip();

	// String should become Token here.
	boolean endsWith(String slice);

	/**
	 * Checks if this token sequence ends with the given token.
	 *
	 * @param token The token to check for at the end
	 * @return true if this sequence ends with the given token
	 */
	boolean endsWith(Token token);

	int length();

	TokenSequence substring(int start, int end);

	// String should become Token here eventually.
	Option<Integer> indexOf(String infix);

	/**
	 * Finds the index of the given token in this sequence.
	 *
	 * @param token The token to search for
	 * @return Some(index) if found, None otherwise
	 */
	Option<Integer> indexOf(Token token);

	// This will be removed into a proper division operation at some point.
	List<TokenSequence> split(String regex);

	// We will eventually remove this because this is nonsensical in a list of
	// tokens.
	Option<Character> charAt(int index);

	// String should become Token here eventually.
	Option<Integer> lastIndexOf(String infix);

	/**
	 * Finds the last index of the given token in this sequence.
	 *
	 * @param token The token to search for
	 * @return Some(index) if found, None otherwise
	 */
	Option<Integer> lastIndexOf(Token token);

	// Remove, because we shouldn't have a notion of "String".
	@Deprecated
	String value();

	String display();

	// Functionally equivalent to wrapping String in a TokenSequence and calling
	// appendSequence.
	TokenSequence appendSlice(String delimiter);

	TokenSequence appendSequence(TokenSequence sequence);
}
