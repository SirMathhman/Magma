package magma.compile.rule;

import magma.Tuple;
import magma.list.Joiner;
import magma.list.List;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

public class KeepLastMerger implements Merger {
	@Override
	public Option<Tuple<Slice, Slice>> merge(List<Slice> segments, String delimiter) {
		if (segments.size() < 2) return new None<Tuple<Slice, Slice>>();

		// Join all but last element
		final Slice left = new RootSlice(segments.subListOrEmpty(0, segments.size() - 1)
																						 .stream()
																						 .map(Slice::value)
																						 .collect(new Joiner(delimiter)));

		final Option<Slice> lastOpt = segments.getLast();
		if (lastOpt instanceof Some<Slice>(Slice right))
			return new Some<Tuple<Slice, Slice>>(new Tuple<Slice, Slice>(left, right));
		return new None<Tuple<Slice, Slice>>();
	}
}
