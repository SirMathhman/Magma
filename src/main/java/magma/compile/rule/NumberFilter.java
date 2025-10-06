package magma.compile.rule;

import java.util.stream.IntStream;

public class NumberFilter implements Filter {
	public static Filter Filter = new NumberFilter();

	@Override
	public boolean test(String input) {
		final String s;
		if (input.startsWith("-")) s = input.substring(1);
		else s = input;
		return IntStream.range(0, s.length()).mapToObj(s::charAt).allMatch(Character::isDigit);
	}

	@Override
	public String createErrorMessage() {
		return "Not a number";
	}
}
