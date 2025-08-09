package magma.params;

/**
 * Parameter object for binary operation validation methods.
 * Groups related parameters to reduce method parameter count.
 */
public record BinaryOperationParams(
        String expression,
        int operatorIndex,
        String operator,
        String operationName
) {
    /**
     * Gets the left operand from the expression.
     *
     * @return the left operand
     */
    public String leftOperand() {
        return expression.substring(0, operatorIndex).trim();
    }

    /**
     * Gets the right operand from the expression.
     *
     * @return the right operand
     */
    public String rightOperand() {
        if (operator.length() == 2) return expression.substring(operatorIndex + 2).trim();
			return expression.substring(operatorIndex + operator.length()).trim();
		}
}
