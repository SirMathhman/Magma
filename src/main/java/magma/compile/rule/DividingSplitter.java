package magma.compile.rule;

import magma.Tuple;
import magma.option.Option;

import java.util.List;

public record DividingSplitter(Divider divider, Merger merger) implements Splitter {

	public DividingSplitter(Divider divider) {
		this(divider, new KeepFirstMerger());
	}

	// Factory methods for convenience
	public static DividingSplitter keepFirst(Divider divider) {
		return new DividingSplitter(divider, new KeepFirstMerger());
	}

	public static DividingSplitter keepLast(Divider divider) {
		return new DividingSplitter(divider, new KeepLastMerger());
	}

	@Override
	public Option<Tuple<String, String>> split(String input) {
		final List<String> segments = divider.divide(input).toList();
		final String delimiter = divider.delimiter();

		return merger.merge(segments, delimiter);
	}
}
