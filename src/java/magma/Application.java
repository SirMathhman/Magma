package magma;

public class Application {
	private static final String[] VALID_TYPES = {"U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64", ""};
	
	public static String run(String input) throws ApplicationException {
		if (input == null || input.isEmpty()) {
			throw new ApplicationException();
		}
		
		String numberPart = input;
		String typePart = "";
		
		// Extract type part if present
		for (String type : VALID_TYPES) {
			if (!type.isEmpty() && input.endsWith(type)) {
				numberPart = input.substring(0, input.length() - type.length());
				typePart = type;
				break;
			}
		}
		
		// Validate number part
		try {
			Integer.parseInt(numberPart);
			return numberPart;
		} catch (NumberFormatException e) {
			throw new ApplicationException();
		}
	}
}
