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
     * Checks if the input is a range expression (e.g., "100-200U8").
     *
     * @param input The input string to check
     * @return true if it's a range expression, false otherwise
     */
    private static boolean isRangeExpression(final String input) {
        final String trimmed = input.trim();
        final int dashIndex = trimmed.indexOf('-');
        if (dashIndex <= 0 || dashIndex >= trimmed.length() - 1) {
            return false;
        }
        // Check if there are no spaces around the dash
        if (dashIndex > 0 && dashIndex < trimmed.length() - 1
                && trimmed.charAt(dashIndex - 1) != ' '
                && trimmed.charAt(dashIndex + 1) != ' ') {
            // Check if part before dash is numeric
            final String beforeDash = trimmed.substring(0, dashIndex);
            final String numericBefore = StringParser.extractLeadingNumeric(
                    beforeDash);
            if (numericBefore.length() == beforeDash.length()) {
                // Check if part after dash has units
                final String afterDash = trimmed.substring(dashIndex + 1);
                if (StringParser.hasUnits(afterDash)) {
                    return true;
                }
            }
        }
        return false;
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
        // Check for range expressions (e.g., "100-200U8")
        if (isRangeExpression(input)) {
            return new Err<>("Range expressions are not supported");
        }
        // Check if input contains any arithmetic operator
        if (input.contains(" + ") || input.contains(" - ")
                || input.contains(" * ")) {
            // Check if it contains multiplication with addition/subtraction
            if (input.contains(" * ")
                    && (input.contains(" + ") || input.contains(" - "))) {
                return ArithmeticProcessor.processWithPrecedence(input);
            }
            // Check if it contains both addition and subtraction
            if (input.contains(" + ") && input.contains(" - ")) {
                return ArithmeticProcessor.processMixedArithmetic(input);
            }
            // Single operator type
            if (input.contains(" + ")) {
                return ArithmeticProcessor.processArithmetic(input, " \\+ ",
                        new ArithmeticProcessor.OperandConfig(0,
                                Integer::sum, 0));
            }
            if (input.contains(" - ")) {
                return ExpressionParser.splitAndValidate(input, " - ")
                        .flatMap(operands -> {
                            final int firstValue = Integer.parseInt(
                                    StringParser.extractLeadingNumeric(
                                            operands[0].trim()));
                            return ArithmeticProcessor.processOperands(operands,
                                    new ArithmeticProcessor.OperandConfig(
                                            firstValue, (a, b) -> a - b, 1));
                        });
            }
            if (input.contains(" * ")) {
                return ArithmeticProcessor.processArithmetic(input, " \\* ",
                        new ArithmeticProcessor.OperandConfig(1,
                                (a, b) -> a * b, 0));
            }
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
