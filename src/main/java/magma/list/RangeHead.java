package magma.list;

import magma.option.None;
import magma.option.Option;
import magma.option.Some;

public final class RangeHead implements Head<Integer> {
	private final int length;
	private int counter = 0;

	public RangeHead(int length) {this.length = length;}

	@Override
	public Option<Integer> next() {
		if (counter >= length) return new None<Integer>();

		final int current = counter;
		counter++;
		return new Some<Integer>(current);
	}
}
