package magma;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles mapping between Magma types and C types.
 */
public class TypeMapper {
	private static final Map<String, String> TYPE_MAPPING = new HashMap<>();

	static {
		// Signed integer types
		TYPE_MAPPING.put("I8", "int8_t");
		TYPE_MAPPING.put("I16", "int16_t");
		TYPE_MAPPING.put("I32", "int32_t");
		TYPE_MAPPING.put("I64", "int64_t");

		// Unsigned integer types
		TYPE_MAPPING.put("U8", "uint8_t");
		TYPE_MAPPING.put("U16", "uint16_t");
		TYPE_MAPPING.put("U32", "uint32_t");
		TYPE_MAPPING.put("U64", "uint64_t");

		// Boolean type
		TYPE_MAPPING.put("Bool", "bool");
	}

	/**
	 * Gets the C type corresponding to the given Magma type.
	 *
	 * @param magmaType The Magma type (e.g., "U8", "I32", "Bool")
	 * @return The corresponding C type, or null if the type is not supported
	 */
	public static String getCType(String magmaType) {
		return TYPE_MAPPING.get(magmaType);
	}
}