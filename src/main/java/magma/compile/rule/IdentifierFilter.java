package magma.compile.rule;

import java.util.stream.IntStream;

public class IdentifierFilter implements Filter {
	public static Filter Identifier = new IdentifierFilter();

	@Override
	public boolean test(String input) {
		return IntStream.range(0, input.length())
										.mapToObj(input::charAt)
										.allMatch(ch -> Character.isLetterOrDigit(ch) || ch == '_');
	}

	@Override
	public String createErrorMessage() {
		return "Not an identifier";
	}
}
