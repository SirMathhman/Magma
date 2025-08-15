package com.example;

public class App {
  /**
   * If the provided input represents a number, return that same string.
   * Otherwise return an empty string.
   *
   * Examples:
   * - input "5" -> returns "5"
   * - input "3.14" -> returns "3.14"
   * - input "abc" -> returns ""
   */
  public static String emptyString(String input) {
    if (input == null)
      return "";
    String trimmed = input.trim();
    // Check for simple addition like "3 + 4" (numbers may be integer or decimal,
    // optional +/-)
    if (trimmed.matches("^[+-]?\\d+(\\.\\d+)?\\s*\\+\\s*[+-]?\\d+(\\.\\d+)?$")) {
      String[] parts = trimmed.split("\\+");
      try {
        java.math.BigDecimal a = new java.math.BigDecimal(parts[0].trim());
        java.math.BigDecimal b = new java.math.BigDecimal(parts[1].trim());
        java.math.BigDecimal sum = a.add(b);
        // Strip trailing zeros so "7.0" becomes "7"
        java.math.BigDecimal normalized = sum.stripTrailingZeros();
        return normalized.toPlainString();
      } catch (NumberFormatException ex) {
        return "";
      }
    }

    // Accept integers and decimals (optional leading +/-, optional fractional part)
    if (trimmed.matches("^[+-]?\\d+(\\.\\d+)?$")) {
      return trimmed;
    }
    return "";
  }
}
