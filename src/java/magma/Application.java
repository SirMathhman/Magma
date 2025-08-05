package magma;

public class Application {
	public static String run(String input) throws ApplicationException {
		if (input == null) {
			throw new ApplicationException("Input cannot be null");
		}

		// Handle boolean values
		if (input.equals("true")) {
			return "1";
		}
		if (input.equals("false")) {
			return "0";
		}

		// Handle plain integers
		try {
			Integer.parseInt(input);
			return input;
		} catch (NumberFormatException e) {
			// Not a plain integer, continue
		}

		// Handle typed integers (e.g., "123U64" or "456I8")
		if (input.matches("\\d+[UI]\\d+")) {
			String numericPart = input.replaceAll("[UI]\\d+$", "");
			try {
				Integer.parseInt(numericPart);
				return numericPart;
			} catch (NumberFormatException e) {
				throw new ApplicationException("Invalid numeric part in typed integer: " + input, e);
			}
		}

		// If we get here, the input is invalid
		throw new ApplicationException("Invalid input format: " + input);
	}
}