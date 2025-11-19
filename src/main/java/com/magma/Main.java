package com.magma;

import com.magma.result.Err;
import com.magma.result.Ok;
import com.magma.result.Result;

/**
 * Main entry point for the Magma application.
 */
public final class Main {
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private Main() {
        // Utility class
    }

    /**
     * Checks if input is a boolean literal and returns it.
     *
     * @param input The input string to check
     * @return Ok with boolean value if it's a boolean literal, empty Optional
     *         otherwise
     */
    private static java.util.Optional<Result<String, String>>
            checkBooleanLiteral(final String input) {
        final String trimmed = input.trim();
        if ("true".equals(trimmed)) {
            return java.util.Optional.of(new Ok<>("true"));
        }
        if ("false".equals(trimmed)) {
            return java.util.Optional.of(new Ok<>("false"));
        }
        return java.util.Optional.empty();
    }

    /**
     * Processes arithmetic expressions based on operator types.
     *
     * @param input The input string containing the expression
     * @return Result with the evaluated expression, or empty Optional if not
     *         an arithmetic expression
     */
    private static java.util.Optional<Result<String, String>>
            processArithmeticExpression(final String input) {
        if (input.contains(" + ") || input.contains(" - ")
                || input.contains(" * ")) {
            // Check if it contains multiplication with addition/subtraction
            if (input.contains(" * ")
                    && (input.contains(" + ") || input.contains(" - "))) {
                return java.util.Optional.of(
                        ArithmeticProcessor.processWithPrecedence(input));
            }
            // Check if it contains both addition and subtraction
            if (input.contains(" + ") && input.contains(" - ")) {
                return java.util.Optional.of(
                        ArithmeticProcessor.processMixedArithmetic(input));
            }
            // Single operator type
            if (input.contains(" + ")) {
                return java.util.Optional.of(
                        ArithmeticProcessor.processArithmetic(input, " \\+ ",
                                new ArithmeticProcessor.OperandConfig(0,
                                        Integer::sum, 0)));
            }
            if (input.contains(" - ")) {
                return java.util.Optional.of(
                        ExpressionParser.splitAndValidate(input, " - ")
                                .flatMap(operands -> {
                                    final int firstValue = Integer.parseInt(
                                            StringParser.extractLeadingNumeric(
                                                    operands[0].trim()));
                                    final ArithmeticProcessor.OperandConfig
                                            config =
                                            new ArithmeticProcessor
                                                    .OperandConfig(
                                                    firstValue,
                                                    (a, b) -> a - b, 1);
                                    return ArithmeticProcessor.processOperands(
                                            operands, config);
                                }));
            }
            if (input.contains(" * ")) {
                return java.util.Optional.of(
                        ArithmeticProcessor.processArithmetic(input, " \\* ",
                                new ArithmeticProcessor.OperandConfig(1,
                                        (a, b) -> a * b, 0)));
            }
        }
        return java.util.Optional.empty();
    }

    /**
     * Interprets a string and evaluates arithmetic expressions or extracts
     * the leading numeric part.
     *
     * @param input The input string to interpret (assumed to be present
     *              and non-empty)
     * @return A Result containing the evaluated result or leading numeric
     *         part of the string wrapped in Ok
     */
    public static Result<String, String> interpret(final String input) {
        // Check for boolean literals
        final java.util.Optional<Result<String, String>> booleanResult =
                checkBooleanLiteral(input);
        if (booleanResult.isPresent()) {
            return booleanResult.get();
        }
        // Check for arithmetic expressions
        final java.util.Optional<Result<String, String>> arithmeticResult =
                processArithmeticExpression(input);
        if (arithmeticResult.isPresent()) {
            return arithmeticResult.get();
        }
        // Fall back to extracting leading numeric part
        // Check value range for single values with units
        final Result<String, String> rangeCheck =
                OperandValidator.checkValueRange(input);
        if (rangeCheck instanceof Err<String, String>) {
            return rangeCheck;
        }
        return new Ok<>(StringParser.extractLeadingNumeric(input));
    }
}
