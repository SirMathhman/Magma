package magma;

/**
 * Handles processing of values in variable declarations.
 * <p>
 * This class provides functionality for cleaning, extracting, and validating values
 * in variable declarations. It works alongside the TypeMapper and Compiler classes.
 */
public class ValueProcessor {

	/**
	 * Cleans the value section by removing type suffix if present.
	 *
	 * @param valueSection the value section to clean
	 * @param typeSuffix   the type suffix to remove, if any
	 * @return the cleaned value section
	 */
	public String cleanValueSection(String valueSection, String typeSuffix) {
		if (typeSuffix != null) {
			return valueSection.replace(typeSuffix, "");
		}
		return valueSection;
	}

	/**
	 * Extracts the raw value from a value section (removes "=" and trims).
	 *
	 * @param valueSection the value section
	 * @return the raw value
	 */
	public String extractRawValue(String valueSection) {
		String rawValue = valueSection.substring(valueSection.indexOf("=") + 1).trim();
		// Remove the semicolon if it exists
		if (rawValue.endsWith(";")) {
			rawValue = rawValue.substring(0, rawValue.length() - 1).trim();
		}
		return rawValue;
	}

	/**
	 * Checks if a value is a variable reference (has no digits and no type suffix).
	 *
	 * @param value the value to check
	 * @return true if the value is a variable reference, false otherwise
	 */
	public boolean isVariableReference(String value) {
		return !value.matches(".*[0-9].*") && !value.endsWith("I8") && !value.endsWith("I16") && !value.endsWith("I32") &&
					 !value.endsWith("I64") && !value.endsWith("U8") && !value.endsWith("U16") && !value.endsWith("U32") &&
					 !value.endsWith("U64");
	}
}