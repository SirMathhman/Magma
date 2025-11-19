package com.magma;

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
     * Main method to start the application.
     *
     * @param args Command line arguments
     */
    public static void main(final String[] args) {
        System.out.println("Hello, Magma!");
    }

    /**
     * Interprets a string and extracts the leading numeric part.
     *
     * @param input The input string to interpret
     * @return The leading numeric part of the string
     */
    public static String interpret(final String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            if (Character.isDigit(c)) {
                result.append(c);
            } else {
                break;
            }
        }
        return result.toString();
    }
}

