package magma.compile.rule;

import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.Tuple;

public record InfixSplitter(String infix, Locator locator) implements Splitter {
	@Override
	public Option<Tuple<String, String>> split(String input) {
		return switch (locator.locate(input, infix)) {
			case None<Integer> _ -> new None<>();
			case Some<Integer>(Integer index) -> {
				final String left = input.substring(0, index);
				final String right = input.substring(index + infix.length());
				yield new Some<>(new Tuple<>(left, right));
			}
		};
	}
}
