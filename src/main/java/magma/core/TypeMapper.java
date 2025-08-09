package magma.core;

/**
 * Handles mapping between TypeScript/JavaScript types and C types.
 * <p>
 * This class provides functionality for mapping type names and detecting type suffixes
 * in value expressions. It's used by the compiler to handle type conversions.
 */
public class TypeMapper {
	/**
	 * Maps TypeScript/JavaScript type to corresponding C type.
	 *
	 * @param type the TypeScript/JavaScript type (I8, I16, I32, I64, U8, U16, U32, U64)
	 * @return the corresponding C type
	 */
	public String mapTypeToC(String type) {
		return switch (type) {
			case "I8" -> "int8_t";
			case "I16" -> "int16_t";
			case "I32" -> "int32_t";
			case "I64" -> "int64_t";
			case "U8" -> "uint8_t";
			case "U16" -> "uint16_t";
			case "U32" -> "uint32_t";
			case "U64" -> "uint64_t";
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
		String[] typeSuffixes = {"I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64"};
		for (String suffix : typeSuffixes)
			if (valueSection.contains(suffix)) return suffix;
		return null;
	}
}
