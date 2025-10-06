package magma.compile.rule;

import magma.Tuple;
import magma.list.List;
import magma.option.Option;

public record DividingSplitter(Divider divider, Merger merger) implements Splitter {
	public DividingSplitter(Divider divider) {
		this(divider, new KeepFirstMerger());
	}

	// Factory methods for convenience
	public static DividingSplitter KeepFirst(Divider divider) {
		return new DividingSplitter(divider, new KeepFirstMerger());
	}

	public static DividingSplitter KeepLast(Divider divider) {
		return new DividingSplitter(divider, new KeepLastMerger());
	}

	@Override
	public Option<Tuple<String, String>> split(String input) {
		final List<String> segments = divider.divide(input).toList();
		final String delimiter = divider.delimiter();

		return merger.merge(segments, delimiter);
	}

	@Override
	public String createErrorMessage() {
		return "No segments found.";
	}

	@Override
	public String merge(String left, String right) {
		return left + divider.delimiter() + right;
	}
}
