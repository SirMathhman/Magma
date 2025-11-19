package com.magma;

/**
 * Utility class for parsing strings to extract numeric and unit parts.
 */
public final class StringParser {
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private StringParser() {
        // Utility class
    }

    /**
     * Extracts the leading numeric part from a string.
     *
     * @param str The string to extract from
     * @return The leading numeric part (including negative sign if present)
     */
    public static String extractLeadingNumeric(final String str) {
        final StringBuilder result = new StringBuilder();
        int startIndex = 0;
        // Check for negative sign at the start
        if (str.length() > 0 && str.charAt(0) == '-') {
            result.append('-');
            startIndex = 1;
        }
        for (int i = startIndex; i < str.length(); i++) {
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
    public static boolean hasUnits(final String str) {
        final String numeric = extractLeadingNumeric(str);
        return numeric.length() < str.length();
    }

    /**
     * Extracts the units (non-numeric part) from a string.
     *
     * @param str The string to extract units from
     * @return The units part, or empty string if no units
     */
    public static String extractUnits(final String str) {
        final String numeric = extractLeadingNumeric(str);
        if (numeric.length() < str.length()) {
            return str.substring(numeric.length());
        }
        return "";
    }
}

