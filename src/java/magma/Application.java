package magma;

public class Application {
	public static String run(String input) throws ApplicationException {
		try {
			Integer.parseInt(input);
			return input;
		} catch (NumberFormatException e) {
			throw new ApplicationException();
		}
	}
}
