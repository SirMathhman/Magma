package magma.compile.rule;

import magma.list.List;
import magma.option.None;
import magma.option.Option;

/*
An empty list of tokens.
 */
public record EmptyTokenSequence() implements TokenSequence {
	// ========== Token-based access methods ==========

	@Override
	public Option<Token> getFirst() {
		return new None<Token>();
	}

	@Override
	public Option<Token> getLast() {
		return new None<Token>();
	}

	@Override
	public Option<Token> getAt(int index) {
		return new None<Token>();
	}

	// ========== Legacy methods ==========

	/*
	 * Current implementation is a TODO.
	 */
	@Override
	public boolean equalsSlice(String value) {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean startsWith(String slice) {
		return false;
	}

	@Override
	public boolean startsWith(Token token) {
		return false;
	}

	@Override
	public TokenSequence substring(int extent) {
		return null;
	}

	@Override
	public TokenSequence strip() {
		return null;
	}

	@Override
	public boolean endsWith(String slice) {
		return false;
	}

	@Override
	public boolean endsWith(Token token) {
		return false;
	}

	@Override
	public int length() {
		return 0;
	}

	@Override
	public TokenSequence substring(int start, int end) {
		return null;
	}

	@Override
	public Option<Integer> indexOf(String infix) {
		return null;
	}

	@Override
	public Option<Integer> indexOf(Token token) {
		return new None<Integer>();
	}

	@Override
	public List<TokenSequence> split(String regex) {
		return null;
	}

	@Override
	public Option<Character> charAt(int index) {
		return null;
	}

	@Override
	public Option<Integer> lastIndexOf(String infix) {
		return null;
	}

	@Override
	public Option<Integer> lastIndexOf(Token token) {
		return new None<Integer>();
	}

	@Override
	public String value() {
		return "";
	}

	@Override
	public String display() {
		return "";
	}

	@Override
	public TokenSequence appendSlice(String delimiter) {
		return null;
	}

	@Override
	public TokenSequence appendSequence(TokenSequence sequence) {
		return null;
	}
}
