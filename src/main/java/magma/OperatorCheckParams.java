package magma;

/**
 * Parameter object for operator checking methods.
 * Groups related parameters to reduce parameter count.
 */
public record OperatorCheckParams(
        String expression,
        int index,
        String operator
) {
}