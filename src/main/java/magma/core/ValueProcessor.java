package magma.core;

/**
 * Handles processing of values in variable declarations.
 * <p>
 * This class provides functionality for cleaning, extracting, and validating values
 * in variable declarations. It works alongside the TypeMapper and Compiler classes.
 * <p>
 * It supports the address-of operator (&) for pointer operations, which is used to get
 * the memory address of a variable (e.g., "&x" returns the address of variable x).
 * The address-of operator is typically used with pointer types (e.g., let y : *I32 = &x;).
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
		if (typeSuffix != null) return valueSection.replace(typeSuffix, "");
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
		if (rawValue.endsWith(";")) rawValue = rawValue.substring(0, rawValue.length() - 1).trim();
		return rawValue;
	}

	/**
	 * Checks if a value is a variable reference (has no digits, no type suffix, and is not a boolean literal).
	 * Also handles variables with address-of operator.
	 *
	 * @param value the value to check
	 * @return true if the value is a variable reference, false otherwise
	 */
	public boolean isVariableReference(String value) {
		// Remove address-of operator if present for the check
		String checkValue = value;
		if (isAddressOf(checkValue)) {
			checkValue = extractVariableFromAddressOf(checkValue);
		}

		// Check for numeric content
		if (checkValue.matches(".*[0-9].*")) return false;

		// Check for type suffixes
		if (isTypeSuffix(checkValue)) return false;

		// Check for boolean literals
		return !isBooleanLiteral(checkValue);
	}

	/**
	 * Checks if a value ends with a type suffix.
	 *
	 * @param value the value to check
	 * @return true if the value ends with a type suffix, false otherwise
	 */
	private boolean isTypeSuffix(String value) {
		String[] typeSuffixes = {"I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64", "F32", "F64"};
		for (String suffix : typeSuffixes) {if (value.endsWith(suffix)) return true;}
		return false;
	}

	/**
	 * Checks if a value is a boolean literal.
	 *
	 * @param value the value to check
	 * @return true if the value is a boolean literal, false otherwise
	 */
	private boolean isBooleanLiteral(String value) {
		return value.equals("true") || value.equals("false");
	}

	/**
	 * Checks if a value uses the address-of operator.
	 *
	 * @param value the value to check
	 * @return true if the value uses the address-of operator, false otherwise
	 */
	public boolean isAddressOf(String value) {
		return value.trim().startsWith("&") && value.trim().length() > 1;
	}

	/**
	 * Extracts the variable name from an address-of expression.
	 *
	 * @param value the address-of expression
	 * @return the variable name without the address-of operator
	 */
	public String extractVariableFromAddressOf(String value) {
		if (isAddressOf(value)) {
			return value.trim().substring(1).trim();
		}
		return value;
	}
}
