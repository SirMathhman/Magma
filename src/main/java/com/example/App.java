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
    // Accept integers and decimals (optional leading +/-, optional fractional part)
    if (trimmed.matches("^[+-]?\\d+(\\.\\d+)?$")) {
      return trimmed;
    }
    return "";
  }
}
