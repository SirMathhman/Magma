package com.magma;

import java.util.function.BinaryOperator;

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
     * Extracts the leading numeric part from a string.
     *
     * @param str The string to extract from
     * @return The leading numeric part
     */
    private static String extractLeadingNumeric(final String str) {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            final char c = str.charAt(i);
            if (Character.isDigit(c)) {
                result.append(c);
            } else {
                break;
            }
        }
        return result.toString();
    }

    /**
     * Checks if a string has units (non-numeric characters after the leading
     * digits).
     *
     * @param str The string to check
     * @return true if the string has units, false otherwise
     */
    private static boolean hasUnits(final String str) {
        final String numeric = extractLeadingNumeric(str);
        return numeric.length() < str.length();
    }

    /**
     * Extracts the units (non-numeric part) from a string.
     *
     * @param str The string to extract units from
     * @return The units part, or empty string if no units
     */
    private static String extractUnits(final String str) {
        final String numeric = extractLeadingNumeric(str);
        if (numeric.length() < str.length()) {
            return str.substring(numeric.length());
        }
        return "";
    }

    /**
     * Checks if all operands with units have matching units.
     *
     * @param operands Array of operand strings
     * @return Err if units mismatch, Ok with empty string if units match
     */
    private static Result<String, String> checkUnitMismatches(
            final String[] operands) {
        for (int i = 0; i < operands.length; i++) {
            final String operandI = operands[i].trim();
            if (hasUnits(operandI)) {
                final String unitsI = extractUnits(operandI);
                for (int j = i + 1; j < operands.length; j++) {
                    final String operandJ = operands[j].trim();
                    if (hasUnits(operandJ)) {
                        final String unitsJ = extractUnits(operandJ);
                        if (!unitsI.equals(unitsJ)) {
                            return new Err<>(
                                    "Cannot operate on values with different "
                                    + "units");
                        }
                    }
                }
            }
        }
        return new Ok<>("");
    }

    /**
     * Validates operands and checks for unit mismatches.
     *
     * @param operands Array of operand strings
     * @return Err if units mismatch, Ok with operands if valid
     */
    private static Result<String[], String> validateOperands(
            final String[] operands) {
        return checkUnitMismatches(operands)
                .map(ignored -> operands);
    }

    /**
     * Processes operands using the given binary operator.
     *
     * @param operands Array of validated operand strings
     * @param initialValue The initial value to start with
     * @param operator The binary operator to apply
     * @param startIndex The index to start processing from
     * @return Result with the computed value
     */
    private static Result<String, String> processOperands(
            final String[] operands,
            final int initialValue,
            final BinaryOperator<Integer> operator,
            final int startIndex) {
        int result = initialValue;
        for (int i = startIndex; i < operands.length; i++) {
            final String numeric = extractLeadingNumeric(
                    operands[i].trim());
            result = operator.apply(result, Integer.parseInt(numeric));
        }
        return new Ok<>(String.valueOf(result));
    }

    /**
     * Splits and validates operands from input.
     *
     * @param input The input string containing the expression
     * @param operatorPattern The operator pattern to split on
     * @return Err if validation fails, Ok with validated operands otherwise
     */
    private static Result<String[], String> splitAndValidate(
            final String input,
            final String operatorPattern) {
        final String[] operands = input.split(operatorPattern);
        return validateOperands(operands);
    }

    /**
     * Processes an arithmetic expression with the given operator.
     *
     * @param input The input string containing the expression
     * @param operatorPattern The operator pattern to split on
     * @param operation The binary operator to apply
     * @param initialValue The initial value for the operation
     * @param startIndex The index to start processing from
     * @return Result with the evaluated expression
     */
    private static Result<String, String> processArithmetic(
            final String input,
            final String operatorPattern,
            final BinaryOperator<Integer> operation,
            final int initialValue,
            final int startIndex) {
        return splitAndValidate(input, operatorPattern)
                .flatMap(operands -> processOperands(operands, initialValue,
                        operation, startIndex));
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
        // Check if input contains an arithmetic operator
        if (input.contains(" + ")) {
            return processArithmetic(input, " \\+ ", Integer::sum, 0, 0);
        }
        if (input.contains(" - ")) {
            return splitAndValidate(input, " - ")
                    .flatMap(operands -> {
                        final int firstValue = Integer.parseInt(
                                extractLeadingNumeric(operands[0].trim()));
                        return processOperands(operands, firstValue,
                                (a, b) -> a - b, 1);
                    });
        }
        // Fall back to extracting leading numeric part
        return new Ok<>(extractLeadingNumeric(input));
    }
}

