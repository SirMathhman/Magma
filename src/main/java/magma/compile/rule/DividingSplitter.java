package magma.compile.rule;

import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.Tuple;

import java.util.List;

public record DividingSplitter(Divider divider, SplitMode mode) implements Splitter {

	public enum SplitMode {
		/** Keep first element, join the rest */
		FIRST_REST,
		/** Join all but last element, keep last */
		ALL_BUT_LAST
	}

	public DividingSplitter(Divider divider) {
		this(divider, SplitMode.FIRST_REST);
	}

	@Override
	public Option<Tuple<String, String>> split(String input) {
		final List<String> segments = divider.divide(input).toList();

		if (segments.size() < 2) {
			return new None<>();
		}

		final String delimiter = divider.delimiter();

		return switch (mode) {
			case FIRST_REST -> {
				// Split into first segment and the rest
				final String left = segments.get(0);

				// Rejoin the remaining segments with the delimiter
				final StringBuilder right = new StringBuilder();
				for (int i = 1; i < segments.size(); i++) {
					if (i > 1) {
						right.append(delimiter);
					}
					right.append(segments.get(i));
				}

				yield new Some<>(new Tuple<>(left, right.toString()));
			}
			case ALL_BUT_LAST -> {
				// Join all but last element
				final StringBuilder left = new StringBuilder();
				for (int i = 0; i < segments.size() - 1; i++) {
					if (i > 0) {
						left.append(delimiter);
					}
					left.append(segments.get(i));
				}

				final String right = segments.get(segments.size() - 1);

				yield new Some<>(new Tuple<>(left.toString(), right));
			}
		};
	}
}
