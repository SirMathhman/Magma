package magma.compile.rule;

import magma.Tuple;
import magma.option.Option;

public record InfixSplitter(String infix, Locator locator) implements Splitter {
	@Override
	public Option<Tuple<String, String>> split(String input) {
		return switch (locator.locate(input, infix)) {
			case Option.None<Integer> _ -> new Option.None<>();
			case Option.Some<Integer>(Integer index) -> {
				final String left = input.substring(0, index);
				final String right = input.substring(index + infix.length());
				yield new Option.Some<>(new Tuple<>(left, right));
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
