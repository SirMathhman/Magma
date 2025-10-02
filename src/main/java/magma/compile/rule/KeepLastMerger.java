package magma.compile.rule;

import magma.Tuple;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

import java.util.List;

public class KeepLastMerger implements Merger {
	@Override
	public Option<Tuple<String, String>> merge(List<String> segments, String delimiter) {
		if (segments.size() < 2) return new None<>();

		// Join all but last element
		final String left = String.join(delimiter, segments.subList(0, segments.size() - 1));
		final String right = segments.getLast();
		return new Some<>(new Tuple<>(left, right));
	}
}
