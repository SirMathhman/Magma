package magma.compile.rule;

import magma.Tuple;
import magma.list.List;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

public class KeepFirstMerger implements Merger {
	@Override
	public Option<Tuple<String, String>> merge(List<String> segments, String delimiter) {
		if (segments.size() < 2) return new None<>();

		// Split into first segment and the rest
		final String left = segments.getFirstOrNull();
		final String right = String.join(delimiter, segments.subList(1, segments.size()));
		return new Some<>(new Tuple<>(left, right));
	}
}
