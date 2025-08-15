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
    // Support simple statement sequences with assignments, e.g. "let x = 20; x"
    if (trimmed.contains(";") || trimmed.startsWith("let ")) {
      java.math.BigDecimal res = processStatements(trimmed);
      if (res == null) {
        return "";
      }
      return res.stripTrailingZeros().toPlainString();
    }
    // Accept integers and decimals (optional leading +/-, optional fractional part)
    if (trimmed.matches("^[+-]?\\d+(\\.\\d+)?$")) {
      // preserve original formatting (including leading '+') for plain numeric input
      return trimmed;
    }
    // Delegate expression evaluation to helper (no variables in this call)
    java.math.BigDecimal val = evaluateExpression(trimmed, java.util.Collections.emptyMap());
    if (val != null) {
      return val.stripTrailingZeros().toPlainString();
    }
    return "";
  }

  private static java.math.BigDecimal processStatements(String trimmed) {
    java.util.Map<String, java.math.BigDecimal> vars = new java.util.HashMap<>();
    String[] stmts = trimmed.split(";");
    java.math.BigDecimal last = null;
    for (String stmt : stmts) {
      String s = stmt.trim();
      if (s.isEmpty()) {
        continue;
      }
      if (s.startsWith("let ")) {
        java.util.regex.Matcher assign = java.util.regex.Pattern
            .compile("^let\\s+([A-Za-z_]\\w*)\\s*=\\s*(.+)$")
            .matcher(s);
        if (!assign.matches()) {
          return null;
        }
        String var = assign.group(1);
        String expr = assign.group(2).trim();
        java.math.BigDecimal val = evaluateExpression(expr, vars);
        if (val == null) {
          return null;
        }
        vars.put(var, val);
        last = val;
      } else {
        java.math.BigDecimal val = evaluateExpression(s, vars);
        if (val == null) {
          return null;
        }
        last = val;
      }
    }
    return last;
  }

  private static java.math.BigDecimal evaluateExpression(String expr,
      java.util.Map<String, java.math.BigDecimal> vars) {
    if (expr == null)
      return null;
    String t = expr.trim();
    // variable
    if (t.matches("[A-Za-z_]\\w*")) {
      return vars.get(t);
    }
    // numeric literal
    if (t.matches("^[+-]?\\d+(\\.\\d+)?$")) {
      try {
        return new java.math.BigDecimal(t);
      } catch (NumberFormatException ex) {
        return null;
      }
    }
    // binary op where operands may be numbers or variables
    java.util.regex.Pattern binOp = java.util.regex.Pattern
        .compile("^([A-Za-z_]\\w*|[+-]?\\d+(\\.\\d+)?)\\s*([+\\-*/])\\s*([A-Za-z_]\\w*|[+-]?\\d+(\\.\\d+)?)$");
    java.util.regex.Matcher m = binOp.matcher(t);
    if (m.matches()) {
      try {
        String leftTok = m.group(1);
        String rightTok = m.group(4);
        java.math.BigDecimal a;
        java.math.BigDecimal b;
        if (leftTok.matches("[A-Za-z_]\\w*")) {
          a = vars.get(leftTok);
          if (a == null)
            return null;
        } else {
          a = new java.math.BigDecimal(leftTok);
        }
        if (rightTok.matches("[A-Za-z_]\\w*")) {
          b = vars.get(rightTok);
          if (b == null)
            return null;
        } else {
          b = new java.math.BigDecimal(rightTok);
        }
        switch (m.group(3)) {
          case "+":
            return a.add(b);
          case "-":
            return a.subtract(b);
          case "*":
            return a.multiply(b);
          case "/":
            if (b.compareTo(java.math.BigDecimal.ZERO) == 0)
              return null;
            return a.divide(b, 16, java.math.RoundingMode.HALF_UP);
        }
      } catch (NumberFormatException | ArithmeticException ex) {
        return null;
      }
    }
    // not recognized
    return null;
  }
}
