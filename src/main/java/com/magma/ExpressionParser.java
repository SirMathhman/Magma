package com.magma;

import com.magma.result.Err;
import com.magma.result.Ok;
import com.magma.result.Result;

/**
 * Utility class for parsing arithmetic expressions.
 */
public final class ExpressionParser {
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ExpressionParser() {
        // Utility class
    }

    /**
     * Splits expression into parts and extracts operands and operators.
     *
     * @param input The input string containing the expression
     * @param operatorPattern The regex pattern to split on
     * @return Tuple of operands array and operators array, or Err if invalid
     */
    public static Result<Tuple<String[], String[]>, String> splitExpression(
            final String input, final String operatorPattern) {
        final String[] parts = input.split(operatorPattern);
        if (parts.length < 2) {
            return new Err<>("Invalid expression");
        }
        final String[] operands = new String[(parts.length + 1) / 2];
        final String[] operators = new String[parts.length / 2];
        for (int i = 0; i < parts.length; i += 2) {
            operands[i / 2] = parts[i];
        }
        for (int i = 1; i < parts.length; i += 2) {
            operators[i / 2] = parts[i].trim();
        }
        return new Ok<>(new Tuple<>(operands, operators));
    }

    /**
     * Splits and validates operands from input.
     *
     * @param input The input string containing the expression
     * @param operatorPattern The operator pattern to split on
     * @return Err if validation fails, Ok with validated operands otherwise
     */
    public static Result<String[], String> splitAndValidate(
            final String input,
            final String operatorPattern) {
        final String[] operands = input.split(operatorPattern);
        return OperandValidator.validateOperands(operands);
    }

    /**
     * Splits and validates an expression, then processes it with the given
     * function.
     *
     * @param input The input string containing the expression
     * @param operatorPattern The regex pattern to split on
     * @param processor Function to process validated operands and operators
     * @return Result with the evaluated expression
     */
    public static Result<String, String> splitValidateAndProcess(
            final String input,
            final String operatorPattern,
            final java.util.function.Function<Tuple<String[], String[]>,
                    Result<String, String>> processor) {
        return splitExpression(input, operatorPattern)
                .flatMap(tuple -> {
                    final String[] operands = tuple.first();
                    final String[] operators = tuple.second();
                    return OperandValidator.validateOperands(operands)
                            .flatMap(validOperands -> processor.apply(
                                    new Tuple<>(validOperands, operators)));
                });
    }

    /**
     * A tuple containing two values.
     *
     * @param <A> The type of the first value
     * @param <B> The type of the second value
     * @param first The first value
     * @param second The second value
     */
    public record Tuple<A, B>(A first, B second) {
    }
}

