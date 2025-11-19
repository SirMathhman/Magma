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
     * Interprets a string and returns it unchanged.
     *
     * @param input The input string to interpret
     * @return The same string that was passed in
     */
    public static String interpret(final String input) {
        return input;
    }
}

