package magma.compile.rule;

import magma.Tuple;
import magma.list.Joiner;
import magma.list.List;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

public class KeepFirstMerger implements Merger {
	@Override
	public Option<Tuple<String, String>> merge(List<String> segments, String delimiter) {
		if (segments.size() < 2) return new None<Tuple<String, String>>();

		// Split into first segment and the rest
		final String left = segments.getFirst().orElse(null);
		final String right = segments.subListOrEmpty(1, segments.size()).stream().collect(new Joiner(delimiter));

		return new Some<Tuple<String, String>>(new Tuple<String, String>(left, right));
	}
}
