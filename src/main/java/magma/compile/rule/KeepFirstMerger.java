package magma.compile.rule;

import magma.Tuple;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

import java.util.List;

public class KeepFirstMerger implements Merger {
	@Override
	public Option<Tuple<String, String>> merge(List<String> segments, String delimiter) {
		if (segments.size() < 2) {
			return new None<>();
		}

		// Split into first segment and the rest
		final String left = segments.get(0);

		// Rejoin the remaining segments with the delimiter
		final StringBuilder right = new StringBuilder(); for (int i = 1; i < segments.size(); i++) {
			if (i > 1) {
				right.append(delimiter);
			} right.append(segments.get(i));
		}

		return new Some<>(new Tuple<>(left, right.toString()));
	}
}
