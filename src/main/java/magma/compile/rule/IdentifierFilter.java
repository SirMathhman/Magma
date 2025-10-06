package magma.compile.rule;

import magma.list.Stream;

public class IdentifierFilter implements Filter {
	public static Filter Identifier = new IdentifierFilter();

	@Override
	public boolean test(String input) {
		Stream<Integer> integerStream = Stream.range(0, input.length());
		return integerStream.map(input::charAt).allMatch(ch -> Character.isLetterOrDigit(ch) || ch == '_');
	}

	@Override
	public String createErrorMessage() {
		return "Not an identifier";
	}
}
