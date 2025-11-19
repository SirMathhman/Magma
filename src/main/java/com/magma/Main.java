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
        final Result<String, String> unitCheck =
                checkUnitMismatches(operands);
        if (unitCheck instanceof Err) {
            return new Err<>(((Err<String, String>) unitCheck).getError());
        }
        return new Ok<>(operands);
    }

    /**
     * Processes operands with addition operation.
     *
     * @param operands Array of validated operand strings
     * @return Result with the sum
     */
    private static Result<String, String> processAddition(
            final String[] operands) {
        int sum = 0;
        for (final String operand : operands) {
            final String numeric = extractLeadingNumeric(operand.trim());
            sum += Integer.parseInt(numeric);
        }
        return new Ok<>(String.valueOf(sum));
    }

    /**
     * Processes operands with subtraction operation.
     *
     * @param operands Array of validated operand strings
     * @return Result with the difference
     */
    private static Result<String, String> processSubtraction(
            final String[] operands) {
        int result = Integer.parseInt(
                extractLeadingNumeric(operands[0].trim()));
        for (int i = 1; i < operands.length; i++) {
            final String numeric = extractLeadingNumeric(
                    operands[i].trim());
            result -= Integer.parseInt(numeric);
        }
        return new Ok<>(String.valueOf(result));
    }

    /**
     * Processes an arithmetic expression with the given operator.
     *
     * @param input The input string containing the expression
     * @param operator The operator pattern to split on
     * @param isAddition True for addition, false for subtraction
     * @return Result with the evaluated expression
     */
    private static Result<String, String> processArithmetic(
            final String input,
            final String operator,
            final boolean isAddition) {
        final String[] operands = input.split(operator);
        final Result<String[], String> validated =
                validateOperands(operands);
        if (validated instanceof Err) {
            final Err<String[], String> err =
                    (Err<String[], String>) validated;
            return new Err<>(err.getError());
        }
        final String[] validOperands =
                ((Ok<String[], String>) validated).getValue();
        if (isAddition) {
            return processAddition(validOperands);
        }
        return processSubtraction(validOperands);
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
            return processArithmetic(input, " \\+ ", true);
        }
        if (input.contains(" - ")) {
            return processArithmetic(input, " - ", false);
        }
        // Fall back to extracting leading numeric part
        return new Ok<>(extractLeadingNumeric(input));
    }
}

