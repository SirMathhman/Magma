package magma.compile.rule;

import magma.list.Stream;
import magma.option.Options;

public class IdentifierFilter implements Filter {
	public static final Filter Identifier = new IdentifierFilter();

	@Override
	public boolean test(TokenSequence input) {
		return Stream.range(0, input.length())
								 .map(input::charAt)
								 .flatMap(Options::stream)
								 .allMatch(ch -> Character.isLetterOrDigit(ch) || ch == '_');
	}

	@Override
	public String createErrorMessage() {
		return "Not an identifier";
	}
}
