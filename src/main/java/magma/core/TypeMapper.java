package magma.core;

/**
 * Handles mapping between TypeScript/JavaScript types and C types.
 * <p>
 * This class provides functionality for mapping type names and detecting type suffixes
 * in value expressions. It's used by the compiler to handle type conversions.
 * It also supports pointer types with the syntax "*TYPE" (e.g., "*I32" for a pointer to a 32-bit integer).
 */
public class TypeMapper {
	/**
	 * Maps TypeScript/JavaScript type to corresponding C type.
	 *
	 * @param type the TypeScript/JavaScript type (I8, I16, I32, I64, U8, U16, U32, U64, *TYPE for pointers)
	 * @return the corresponding C type
	 */
	public String mapTypeToC(String type) {
		// Check if it's a pointer type
		if (type.startsWith("*")) {
			// Extract the base type (after the asterisk)
			String baseType = type.substring(1);
			// Map the base type and add the asterisk for C pointer type
			return mapTypeToC(baseType) + "*";
		}

		return switch (type) {
			case "I8" -> "int8_t";
			case "I16" -> "int16_t";
			case "I32" -> "int32_t";
			case "I64" -> "int64_t";
			case "U8" -> "uint8_t";
			case "U16" -> "uint16_t";
			case "U32" -> "uint32_t";
			case "U64" -> "uint64_t";
			case "F32" -> "float";
			case "F64" -> "double";
			case "Bool" -> "bool";
			default -> "int32_t"; // Default to int32_t
		};
	}

	/**
	 * Detects type suffix in the value section of a variable declaration.
	 *
	 * @param valueSection the value section of the declaration
	 * @return the detected type suffix or null if none is found
	 */
	public String detectTypeSuffix(String valueSection) {
		String[] typeSuffixes = {"I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64", "F32", "F64"};

		// First check for exact suffix pattern (number followed by suffix)
		for (String suffix : typeSuffixes) {
			if (valueSection.matches(".*\\d+" + suffix + ".*")) {
				return suffix;
			}
		}

		// Fallback to simple contains check if no pattern match
		for (String suffix : typeSuffixes) {
			if (valueSection.contains(suffix)) {
				return suffix;
			}
		}

		return null;
	}
}
