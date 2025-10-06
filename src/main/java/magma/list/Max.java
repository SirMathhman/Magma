package magma.list;

import magma.option.None;
import magma.option.Option;
import magma.option.Some;

public class Max implements Collector<Integer, Option<Integer>> {
	@Override
	public Option<Integer> initial() {
		return new None<Integer>();
	}

	@Override
	public Option<Integer> fold(Option<Integer> current, Integer element) {
		if (!(current instanceof Some<Integer>(Integer inner))) return new Some<Integer>(element);
		return new Some<Integer>(Math.max(inner, element));
	}
}
