/**
 * Record for mapping Java types to C types.
 * This helps eliminate semantic duplication in type handling.
 */
record TypeMapper(String javaType, String cType, String typePattern) {
	/**
	 * Creates a new TypeMapper.
	 *
	 * @param javaType The Java type (e.g., "I32")
	 * @param cType    The corresponding C type (e.g., "int32_t")
	 */
	public TypeMapper(String javaType, String cType) {
		this(javaType, cType, " : " + javaType + " = ");
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
