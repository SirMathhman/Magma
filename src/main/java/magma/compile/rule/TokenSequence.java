package magma.compile.rule;

import magma.list.List;
import magma.option.Option;

public interface TokenSequence {
	boolean equalsSlice(String value);

	boolean isEmpty();

	boolean startsWith(String slice);

	TokenSequence substring(int extent);

	TokenSequence strip();

	boolean endsWith(String slice);

	int length();

	TokenSequence substring(int start, int end);

	Option<Integer> indexOf(String infix);

	List<TokenSequence> split(String regex);

	Option<Character> charAt(int index);

	Option<Integer> lastIndexOf(String infix);

	@Deprecated
	String value();
}
