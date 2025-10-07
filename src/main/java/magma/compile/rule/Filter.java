package magma.compile.rule;

public interface Filter {
	boolean test(TokenSequence input);

	String createErrorMessage();
}
