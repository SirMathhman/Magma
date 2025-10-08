package magma.compile.rule;

import magma.list.List;
import magma.option.Option;

public interface Slice {
	boolean isEmpty();

	boolean startsWith(String slice);

	Slice substring(int extent);

	Slice strip();

	boolean endsWith(String slice);

	int length();

	Slice substring(int start, int end);

	Option<Integer> indexOf(String infix);

	List<Slice> split(String regex);

	Option<Character> charAt(int index);

	Option<Integer> lastIndexOf(String infix);

	String value();
}
