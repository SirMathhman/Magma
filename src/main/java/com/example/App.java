package com.example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    if (input == null) {
      return "";
    }
    String trimmed = input.trim();
    // Check for simple binary operations like "a + b", "a - b", "a * b", "a / b"
    Pattern binOp = Pattern
        .compile("^([+-]?\\d+(\\.\\d+)?)\\s*([+\\-*/])\\s*([+-]?\\d+(\\.\\d+)?)$");
    Matcher m = binOp.matcher(trimmed);
    if (m.matches()) {
      String left = m.group(1);
      String op = m.group(3);
      String right = m.group(4);
      try {
        BigDecimal a = new BigDecimal(left);
        BigDecimal b = new BigDecimal(right);
        BigDecimal result;
        switch (op) {
          case "+":
            result = a.add(b);
            break;
          case "-":
            result = a.subtract(b);
            break;
          case "*":
            result = a.multiply(b);
            break;
          case "/":
            if (b.compareTo(BigDecimal.ZERO) == 0) {
              return ""; // division by zero -> treat as invalid -> empty string
            }
            // Use a reasonable scale and RoundingMode for division
            result = a.divide(b, 16, RoundingMode.HALF_UP);
            break;
          default:
            return "";
        }
        BigDecimal normalized = result.stripTrailingZeros();
        return normalized.toPlainString();
      } catch (ArithmeticException | NumberFormatException ex) {
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
