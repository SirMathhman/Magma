package com.magma;

import java.util.Optional;

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
     * Interprets a string and extracts the leading numeric part.
     *
     * @param input The input string to interpret, wrapped in Optional
     * @return A Result containing the leading numeric part of the string
     *         wrapped in Ok
     */
    public static Result<String, String> interpret(
            final Optional<String> input) {
        final String inputStr = input.orElse("");
        if (inputStr.isEmpty()) {
            return new Ok<>("");
        }
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < inputStr.length(); i++) {
            final char c = inputStr.charAt(i);
            if (Character.isDigit(c)) {
                result.append(c);
            } else {
                break;
            }
        }
        return new Ok<>(result.toString());
    }
}

