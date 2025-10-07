package magma.compile.rule;

import magma.Tuple;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

public record InfixSplitter(String infix, Locator locator) implements Splitter {
	@Override
	public Option<Tuple<TokenSequence, TokenSequence>> split(TokenSequence tokenSequence) {
		return switch (locator.locate(tokenSequence, infix)) {
			case None<Integer> _ -> new None<Tuple<TokenSequence, TokenSequence>>();
			case Some<Integer>(Integer index) -> {
				final TokenSequence left = tokenSequence.substring(0, index);
				final TokenSequence right = tokenSequence.substring(index + infix.length());
				yield new Some<Tuple<TokenSequence, TokenSequence>>(new Tuple<TokenSequence, TokenSequence>(left, right));
			}
		};
	}

	@Override
	public String createErrorMessage() {
		return "Infix '" + infix + "' not present";
	}

	@Override
	public TokenSequence merge(TokenSequence left, TokenSequence right) {
		return left.appendSlice(infix).appendSequence(right);
	}
}
