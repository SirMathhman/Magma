package magma.compile.rule;

import magma.Tuple;
import magma.list.Joiner;
import magma.list.List;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

public class KeepLastMerger implements Merger {
	@Override
	public Option<Tuple<String, String>> merge(List<String> segments, String delimiter) {
		if (segments.size() < 2) return new None<Tuple<String, String>>();

		// Join all but last element
		final String left = segments.subListOrEmpty(0, segments.size() - 1).stream().collect(new Joiner(delimiter));

		final String right = segments.getLastOrNull();
		return new Some<Tuple<String, String>>(new Tuple<String, String>(left, right));
	}
}
