package magma.compile.rule;

import magma.list.Stream;
import magma.option.Options;

public class NumberFilter implements Filter {
	public static final Filter Filter = new NumberFilter();

	@Override
	public boolean test(TokenSequence input) {
		final TokenSequence s;
		if (input.startsWith("-")) s = input.substring(1);
		else s = input;

		return Stream.range(0, s.length()).map(s::charAt).flatMap(Options::stream).allMatch(Character::isDigit);
	}

	@Override
	public String createErrorMessage() {
		return "Not a number";
	}
}
