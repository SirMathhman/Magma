package magma;

public class Application {
	public static String run(String value) {
		try {
			Integer.parseInt(value);
			return value;
		} catch (NumberFormatException e) {
			throw new ApplicationException();
		}
	}
}
