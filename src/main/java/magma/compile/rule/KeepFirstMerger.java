package magma.compile.rule;

import magma.Tuple;
import magma.option.Option;

import java.util.List;

public class KeepFirstMerger implements Merger {
	@Override
	public Option<Tuple<String, String>> merge(List<String> segments, String delimiter) {
		if (segments.size() < 2) return new Option.None<>();

		// Split into first segment and the rest
		final String left = segments.getFirst();
		final String right = String.join(delimiter, segments.subList(1, segments.size()));
		return new Option.Some<>(new Tuple<>(left, right));
	}
}
