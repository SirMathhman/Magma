package magma;

/**
 * Parameter object for arithmetic type consistency checking.
 * Groups related parameters to reduce method parameter count.
 */
public record ArithmeticTypeCheckParams(
        String leftOperand,
        String rightOperand,
        String leftType,
        String rightType,
        String operationName
) {
    /**
     * Checks if both operands have known types.
     *
     * @return true if both types are not null
     */
    public boolean bothTypesKnown() {
        return leftType != null && rightType != null;
    }

    /**
     * Checks if the types are incompatible.
     *
     * @return true if both types are known and different
     */
    public boolean hasTypeMismatch() {
        return bothTypesKnown() && !leftType.equals(rightType);
    }

    /**
     * Creates an error message for type mismatch.
     *
     * @return a formatted error message
     */
    public String createTypeMismatchMessage() {
        return "Type mismatch in " + operationName + ": Cannot perform " + 
               operationName + " with " + leftType + " and " + rightType + 
               " variables in expression. All numbers in a " + operationName +
               " operation must be of the same type.";
    }
}