package magma.compile.rule;

import magma.Tuple;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

public record InfixSplitter(String infix, Locator locator) implements Splitter {
	@Override
	public Option<Tuple<Slice, Slice>> split(Slice slice) {
		return switch (locator.locate(slice, infix)) {
			case None<Integer> _ -> new None<Tuple<Slice, Slice>>();
			case Some<Integer>(Integer index) -> {
				final Slice left = slice.substring(0, index);
				final Slice right = slice.substring(index + infix.length());
				yield new Some<Tuple<Slice, Slice>>(new Tuple<Slice, Slice>(left, right));
			}
		};
	}

	@Override
	public String createErrorMessage() {
		return "Infix '" + infix + "' not present";
	}

	@Override
	public String merge(String left, String right) {
		return left + infix + right;
	}
}
