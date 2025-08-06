/**
 * Enum for mapping Magma types to C types.
 * This helps eliminate semantic duplication in type handling.
 */
public enum TypeMapper {
	I8("I8", "int8_t"), I16("I16", "int16_t"), I32("I32", "int32_t"), I64("I64", "int64_t"), U8("U8", "uint8_t"),
	U16("U16", "uint16_t"), U32("U32", "uint32_t"), U64("U64", "uint64_t"), Bool("Bool", "bool"), Char("Char", "uint8_t");

	private final String javaType;
	private final String cType;
	private final String typePattern;

	/**
	 * Creates a new TypeMapper.
	 *
	 * @param javaType The Magma type (e.g., "I32")
	 * @param cType    The corresponding C type (e.g., "int32_t")
	 */
	TypeMapper(String javaType, String cType) {
		this.javaType = javaType;
		this.cType = cType;
		this.typePattern = " : " + javaType + " = ";
	}

	/**
	 * Gets the Magma type.
	 *
	 * @return The Magma type
	 */
	public String javaType() {
		return javaType;
	}

	/**
	 * Gets the C type.
	 *
	 * @return The C type
	 */
	public String cType() {
		return cType;
	}

	/**
	 * Gets the type pattern.
	 *
	 * @return The type pattern
	 */
	public String typePattern() {
		return typePattern;
	}

	/**
	 * Checks if a line contains this type.
	 *
	 * @param line The line to check
	 * @return True if the line contains this type
	 */
	public boolean matchesLine(String line) {
		return line.contains(typePattern);
	}
}
