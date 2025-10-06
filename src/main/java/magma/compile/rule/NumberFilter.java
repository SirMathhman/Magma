package magma.compile.rule;

import magma.list.Stream;

public class NumberFilter implements Filter {
	public static Filter Filter = new NumberFilter();

	@Override
	public boolean test(String input) {
		final String s;
		if (input.startsWith("-")) s = input.substring(1);
		else s = input;
		Stream<Integer> integerStream = Stream.range(0, s.length());
		return integerStream.map(s::charAt).allMatch(Character::isDigit);
	}

	@Override
	public String createErrorMessage() {
		return "Not a number";
	}
}
