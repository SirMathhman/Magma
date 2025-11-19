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
                                    "Cannot add values with different units");
                        }
                    }
                }
            }
        }
        return new Ok<>("");
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
            final String[] operands = input.split(" \\+ ");
            // Check for unit mismatches between all pairs of operands
            // with units
            final Result<String, String> unitCheck =
                    checkUnitMismatches(operands);
            if (unitCheck instanceof Err) {
                return unitCheck;
            }
            // Sum all operands
            int sum = 0;
            for (final String operand : operands) {
                final String numeric = extractLeadingNumeric(operand.trim());
                sum += Integer.parseInt(numeric);
            }
            return new Ok<>(String.valueOf(sum));
        }
        // Fall back to extracting leading numeric part
        return new Ok<>(extractLeadingNumeric(input));
    }
}

