package magma;

public class Application {
	public static String run(String input) throws ApplicationException {
		try {
			// First try to parse as a plain integer
			try {
				Integer.parseInt(input);
				return input;
			} catch (NumberFormatException e) {
				// Check if it's a typed integer (e.g., "100U64")
				if (input.matches("\\d+[A-Za-z]\\d+")) {
					// Extract the numeric part
					String numericPart = input.replaceAll("[A-Za-z]\\d+$", "");
					
					// Verify the numeric part is a valid integer
					Integer.parseInt(numericPart);
					return numericPart;
				}
				
				throw new ApplicationException();
			}
		} catch (NumberFormatException e) {
			throw new ApplicationException();
		}
	}
}