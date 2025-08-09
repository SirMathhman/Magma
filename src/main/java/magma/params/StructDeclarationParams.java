package magma.params;

/**
 * Parameters for struct declaration validation.
 * 
 * This class encapsulates the parameters needed for validating and
 * processing struct declarations in the Magma compiler.
 */
public record StructDeclarationParams(
    String statement,
    String structName,
    String body
) {
    /**
     * Creates a new StructDeclarationParams instance.
     *
     * @param statement the struct declaration statement
     * @param structName the name of the struct
     * @param body the body of the struct declaration
     */
    public StructDeclarationParams {
        // Validation happens in the validator class
    }
}