package magma.compile.rule;

public interface Filter {
	boolean test(String input);

	String createErrorMessage();
}
