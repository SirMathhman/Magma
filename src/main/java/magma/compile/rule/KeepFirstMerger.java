package magma.compile.rule;

import magma.Tuple;
import magma.list.Joiner;
import magma.list.List;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

public class KeepFirstMerger implements Merger {
	@Override
	public Option<Tuple<Slice, Slice>> merge(List<Slice> segments, String delimiter) {
		if (segments.size() < 2) return new None<Tuple<Slice, Slice>>();

		// Split into first segment and the rest
		final Slice left = segments.getFirst().orElse(null);
		final Slice right = new Slice(segments.subListOrEmpty(1, segments.size())
																					.stream()
																					.map(Slice::value)
																					.collect(new Joiner(delimiter)));

		return new Some<Tuple<Slice, Slice>>(new Tuple<Slice, Slice>(left, right));
	}
}
