package magma.struct;

/**
 * Parameters for struct declaration validation.
 * <p>
 * This class encapsulates the parameters needed for validating and
 * processing struct declarations in the Magma compiler.
 * <p>
 * Supports both empty structs and structs with fields.
 */
public record StructDeclarationParams(String statement, String structName, String body, boolean hasFields) {
	/**
	 * Creates a new StructDeclarationParams instance.
	 *
	 * @param statement  the struct declaration statement
	 * @param structName the name of the struct
	 * @param body       the body of the struct declaration
	 * @param hasFields  whether the struct has field declarations
	 */
	public StructDeclarationParams {
		// Validation happens in the validator class
	}

	/**
	 * Creates a new StructDeclarationParams instance with no fields.
	 *
	 * @param statement  the struct declaration statement
	 * @param structName the name of the struct
	 * @param body       the body of the struct declaration
	 * @return a new StructDeclarationParams with hasFields set to false
	 */
	public static StructDeclarationParams empty(String statement, String structName, String body) {
		return new StructDeclarationParams(statement, structName, body, false);
	}

	/**
	 * Creates a new StructDeclarationParams instance with fields.
	 *
	 * @param statement  the struct declaration statement
	 * @param structName the name of the struct
	 * @param body       the body of the struct declaration
	 * @return a new StructDeclarationParams with hasFields set to true
	 */
	public static StructDeclarationParams withFields(String statement, String structName, String body) {
		return new StructDeclarationParams(statement, structName, body, true);
	}
}