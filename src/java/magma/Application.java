package magma;

public class Application {
	public static String run(String input) throws ApplicationException {
		try {
			// Try to parse the input as a number
			Integer.parseInt(input);
			// If successful, return the input
			return input;
		} catch (NumberFormatException e) {
			// If parsing fails, throw ApplicationException
			throw new ApplicationException();
		}
	}
}
