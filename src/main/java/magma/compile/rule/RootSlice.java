package magma.compile.rule;

import magma.list.ArrayHead;
import magma.list.HeadedStream;
import magma.list.List;
import magma.list.ListCollector;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

public record RootSlice(String value) implements Slice {
	@Override
	public boolean isEmpty() {
		return value.isEmpty();
	}

	@Override
	public boolean startsWith(String slice) {
		return value.startsWith(slice);
	}

	@Override
	public Slice substring(int extent) {
		return new RootSlice(value.substring(extent));
	}

	@Override
	public Slice strip() {
		return new RootSlice(value.strip());
	}

	@Override
	public boolean endsWith(String slice) {
		return value.endsWith(slice);
	}

	@Override
	public int length() {
		return value.length();
	}

	@Override
	public Slice substring(int start, int end) {
		return new RootSlice(value.substring(start, end));
	}

	@Override
	public Option<Integer> indexOf(String infix) {
		final int index = value.indexOf(infix);
		if (index == -1) return new None<Integer>();
		return new Some<Integer>(index);
	}

	@Override
	public List<Slice> split(String regex) {
		return new HeadedStream<String>(new ArrayHead<String>(value.split(regex))).map(this::copy)
																																							.collect(new ListCollector<Slice>());
	}

	private Slice copy(String value1) {
		return new RootSlice(value1);
	}

	@Override
	public Option<Character> charAt(int index) {
		if (index < value.length()) return new Some<Character>(value.charAt(index));
		else return new None<Character>();
	}

	@Override
	public Option<Integer> lastIndexOf(String infix) {
		final int index = value.lastIndexOf(infix);
		if (index == -1) return new None<Integer>();
		return new Some<Integer>(index);
	}
}
