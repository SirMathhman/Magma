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
        final int plusIndex = input.indexOf(" + ");
        if (plusIndex >= 0) {
            final String leftStr = input.substring(0, plusIndex).trim();
            final String rightStr = input.substring(plusIndex + 3).trim();
            // Check if both operands have units
            if (hasUnits(leftStr) && hasUnits(rightStr)) {
                final String leftUnits = extractUnits(leftStr);
                final String rightUnits = extractUnits(rightStr);
                // Only return error if units are different
                if (!leftUnits.equals(rightUnits)) {
                    return new Err<>("Cannot add values with different units");
                }
            }
            final String leftNumeric = extractLeadingNumeric(leftStr);
            final String rightNumeric = extractLeadingNumeric(rightStr);
            final int left = Integer.parseInt(leftNumeric);
            final int right = Integer.parseInt(rightNumeric);
            final int sum = left + right;
            return new Ok<>(String.valueOf(sum));
        }
        // Fall back to extracting leading numeric part
        return new Ok<>(extractLeadingNumeric(input));
    }
}

