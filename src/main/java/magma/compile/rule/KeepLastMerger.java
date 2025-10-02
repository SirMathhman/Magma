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
		final StringBuilder left = new StringBuilder(); for (int i = 0; i < segments.size() - 1; i++) {
			if (i > 0) left.append(delimiter); left.append(segments.get(i));
		}

		final String right = segments.get(segments.size() - 1);

		return new Some<>(new Tuple<>(left.toString(), right));
	}
}
