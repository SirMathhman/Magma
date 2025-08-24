package org.example;

/** Minimal Intrepreter class. */
public class Intrepreter {
  // intentionally minimal implementation

  private static final String[] TYPE_SUFFIXES = { "I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64" };

  // helper container for parsed operand
  private static final class Operand {
    final int value;
    final String suffix; // null if none
    final int endIndex; // position after the operand in the string

    Operand(int value, String suffix, int endIndex) {
      this.value = value;
      this.suffix = suffix;
      this.endIndex = endIndex;
    }
  }

  // Parse an operand (number with optional suffix) starting at index startIdx in
  // s.
  // Returns an Operand containing parsed integer, suffix, and end index (position
  // after operand).
  private Operand parseOperand(String s, int startIdx) {
    int i = startIdx;
    int len = s.length();
    // skip whitespace
    while (i < len && Character.isWhitespace(s.charAt(i)))
      i++;

    // Parenthesized expression support: ( ... )
    if (i < len && s.charAt(i) == '(') {
      // find matching ')', support nested parentheses
      int depth = 1;
      int j = i + 1;
      while (j < len && depth > 0) {
        char cc = s.charAt(j);
        if (cc == '(')
          depth++;
        else if (cc == ')')
          depth--;
        j++;
      }
      if (depth != 0) {
        throw new NumberFormatException("Unmatched '(' at index " + i);
      }
      // inner is between i+1 and j-1
      String inner = s.substring(i + 1, j - 1);
      String evaluated = interpret(inner.trim());
      // interpret may strip suffixes; attempt to detect suffix on evaluated string
      String suffix = getTypeSuffix(evaluated);
      String numString = suffix == null ? evaluated : stripTypeSuffix(evaluated);
      int val = Integer.parseInt(numString);
      return new Operand(val, suffix, j);
    }

    // optional leading sign for number
    int sign = 1;
    if (i < len && s.charAt(i) == '+') {
      i++;
    } else if (i < len && s.charAt(i) == '-') {
      sign = -1;
      i++;
    }
    // read digits
    int startDigits = i;
    while (i < len && Character.isDigit(s.charAt(i)))
      i++;
    if (startDigits == i) {
      throw new NumberFormatException("Expected number at index " + startIdx);
    }
    String numStr = s.substring(startDigits, i);
    int value = Integer.parseInt(numStr) * sign;

    // read possible suffix
    String foundSuffix = null;
    for (String suffix : TYPE_SUFFIXES) {
      if (i + suffix.length() <= len && s.substring(i, i + suffix.length()).equals(suffix)) {
        foundSuffix = suffix;
        i += suffix.length();
        break;
      }
    }

    return new Operand(value, foundSuffix, i);
  }

  /**
   * Interpret the given input string and return a result string.
   * Current implementation echoes the input.
   *
   * @param input the input string to interpret
   * @return the interpreted string (echoes input)
   */
  public String interpret(String input) {
    if (input == null)
      return null;
    // If the input is a simple binary integer expression like "5 + 7", "5 - 2", or
    // "3 * 4", evaluate it.
    String trimmed = input.trim();
    // using chained left-to-right evaluation below.

    // Parse and evaluate left-to-right chained binary operations (+, -, *)
    try {
      int len = trimmed.length();
      int idx = 0;

      Operand acc = parseOperand(trimmed, idx);
      idx = acc.endIndex;

      while (true) {
        // skip whitespace
        while (idx < len && Character.isWhitespace(trimmed.charAt(idx)))
          idx++;
        if (idx >= len)
          break;
        char op = trimmed.charAt(idx);
        if (op != '+' && op != '-' && op != '*')
          break;
        idx++; // consume operator

        Operand next = parseOperand(trimmed, idx);
        idx = next.endIndex;

        // enforce suffix rules: both none, or both present and equal
        if (acc.suffix == null && next.suffix == null) {
          // ok
        } else if (acc.suffix != null && next.suffix != null) {
          if (!acc.suffix.equals(next.suffix)) {
            throw new InterpretingException("Typed operands must have matching types: '" + input + "'");
          }
        } else {
          throw new InterpretingException("Typed operands are not allowed in arithmetic expressions: '" + input + "'");
        }

        int res;
        switch (op) {
          case '+':
            res = acc.value + next.value;
            break;
          case '-':
            res = acc.value - next.value;
            break;
          case '*':
            res = acc.value * next.value;
            break;
          default:
            res = acc.value;
        }

        // new accumulator keeps the suffix if present (both equal or null)
        acc = new Operand(res, acc.suffix == null ? next.suffix : acc.suffix, idx);
      }

      // if we consumed at least one operator and parsed whole or partial expression,
      // return accumulator as result
      // ensure we've parsed something meaningful (acc was created)
      // If the input contains only a single operand, fall through to suffix stripping
      // below.
      // Return numeric result as string when at least one operator was applied.
      // We'll detect operator presence by checking if trimmed contains any operator
      // char outside numeric suffixes.
      boolean hasOperator = false;
      for (int i = 0; i < trimmed.length(); i++) {
        char c = trimmed.charAt(i);
        if ((c == '+' || c == '*' || c == '-') && !(i == 0 && c == '-')) {
          hasOperator = true;
          break;
        }
      }
      if (hasOperator) {
        return Integer.toString(acc.value);
      }
    } catch (NumberFormatException e) {
      // fall through to suffix stripping / echoing
    }
    // If the input ends with a type-suffix like I8/I16/I32/I64 or U8/U16/U32/U64,
    // strip it.
    return stripTypeSuffix(input);
  }

  /**
   * Return the type suffix if the string ends with a known type suffix, otherwise
   * null.
   */
  private String getTypeSuffix(String str) {
    if (str == null)
      return null;
    for (String suffix : TYPE_SUFFIXES) {
      if (str.endsWith(suffix))
        return suffix;
    }
    return null;
  }

  /**
   * Strip type suffix from input if present.
   */
  private String stripTypeSuffix(String input) {
    if (input == null)
      return null;
    for (String suffix : TYPE_SUFFIXES) {
      if (input.endsWith(suffix)) {
        return input.substring(0, input.length() - suffix.length());
      }
    }
    return input;
  }
}
