package magma.compile.rule;

public class IdentifierFilter implements Filter {
	public static Filter Identifier = new IdentifierFilter();

	@Override
	public boolean test(String input) {
		for (int i = 0; i < input.length(); i++) {if (!Character.isLetterOrDigit(input.charAt(i))) return false;}

		return true;
	}

	@Override
	public String createErrorMessage() {
		return "Not an identifier";
	}
}
