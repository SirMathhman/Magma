package magma;

/**
 * Record to hold parameters for boolean operations.
 * This helps to reduce the parameter count in methods that process boolean operations.
 *
 * @param operator the operator symbol (e.g., "||" or "&&")
 * @param operatorName the human-readable name of the operator (e.g., "logical OR (||)")
 */
public record BooleanOperationParams(String operator, String operatorName) {
    /**
     * Creates parameters for the logical OR operation.
     *
     * @return parameters for logical OR
     */
    public static BooleanOperationParams logicalOr() {
        return new BooleanOperationParams("||", "logical OR (||)");
    }

    /**
     * Creates parameters for the logical AND operation.
     *
     * @return parameters for logical AND
     */
    public static BooleanOperationParams logicalAnd() {
        return new BooleanOperationParams("&&", "logical AND (&&)");
    }
}