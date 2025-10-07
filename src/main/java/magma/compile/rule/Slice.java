package magma.compile.rule;

import magma.list.ArrayHead;
import magma.list.HeadedStream;
import magma.list.List;
import magma.list.ListCollector;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

public record Slice(String value) {
	public boolean isEmpty() {
		return value.isEmpty();
	}

	public boolean startsWith(String slice) {
		return value.startsWith(slice);
	}

	public Slice substring(int extent) {
		return new Slice(value.substring(extent));
	}

	public Slice strip() {
		return new Slice(value.strip());
	}

	public boolean endsWith(String slice) {
		return value.endsWith(slice);
	}

	public int length() {
		return value.length();
	}

	public Slice substring(int start, int end) {
		return new Slice(value.substring(start, end));
	}

	public Option<Integer> indexOf(String infix) {
		final int index = value.indexOf(infix);
		if (index == -1) return new None<Integer>();
		return new Some<Integer>(index);
	}

	public List<Slice> split(String regex) {
		return new HeadedStream<String>(new ArrayHead<String>(value.split(regex))).map(Slice::new)
																																							.collect(new ListCollector<Slice>());
	}

	public Option<Character> charAt(int index) {
		if (index < value.length()) return new Some<Character>(value.charAt(index));
		else return new None<Character>();
	}

	public Option<Integer> lastIndexOf(String infix) {
		final int index = value.lastIndexOf(infix);
		if (index == -1) return new None<Integer>();
		return new Some<Integer>(index);
	}
}
