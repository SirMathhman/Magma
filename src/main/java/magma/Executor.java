package magma;

public class Executor {
	public static String execute(String input) throws ExecutionException {
		if (input == null || input.isEmpty()) {
			return "";
		}
		throw new ExecutionException("Non-empty input not allowed");
	}
}
