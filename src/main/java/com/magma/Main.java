package com.magma;

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
            final int left = Integer.parseInt(leftStr);
            final int right = Integer.parseInt(rightStr);
            final int sum = left + right;
            return new Ok<>(String.valueOf(sum));
        }
        // Fall back to extracting leading numeric part
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            if (Character.isDigit(c)) {
                result.append(c);
            } else {
                break;
            }
        }
        return new Ok<>(result.toString());
    }
}

