package magma.function;

/**
 * Parameters for function declaration processing.
 * This record reduces the number of parameters needed for function declaration validation and processing.
 * 
 * @param statement The original function declaration statement
 * @param functionName The name of the declared function
 * @param parameters The function's parameter list (could be empty)
 * @param returnType The function's return type
 * @param body The function's body content
 */
public record FunctionDeclarationParams(
    String statement,
    String functionName,
    String parameters,
    String returnType,
    String body
) {
    /**
     * Validates that this function declaration has all required components.
     * 
     * @return true if the function declaration is valid, false otherwise
     */
    public boolean isValid() {
        return functionName != null && !functionName.isEmpty() &&
               returnType != null && !returnType.isEmpty() && 
               body != null;
    }
}