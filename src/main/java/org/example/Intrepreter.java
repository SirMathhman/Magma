package org.example;

import java.util.ArrayList;
import java.util.List;

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

    // Parse into lists of operands and operators
    try {
      List<Operand> operands = new ArrayList<>();
      List<Character> operators = new ArrayList<>();

      int len = trimmed.length();
      int idx = 0;
      operands.add(parseOperand(trimmed, idx));
      idx = operands.get(0).endIndex;

      while (true) {
        while (idx < len && Character.isWhitespace(trimmed.charAt(idx)))
          idx++;
        if (idx >= len)
          break;
        char op = trimmed.charAt(idx);
        if (op != '+' && op != '-' && op != '*')
          break;
        operators.add(op);
        idx++; // consume operator
        Operand next = parseOperand(trimmed, idx);
        operands.add(next);
        idx = next.endIndex;
      }

      if (operators.isEmpty()) {
        // no operators found; fall through
      } else {
        // Validate suffix rules pairwise
        for (int k = 0; k < operators.size(); k++) {
          Operand a = operands.get(k);
          Operand b = operands.get(k + 1);
          if (a.suffix == null && b.suffix == null) {
            // ok
          } else if (a.suffix != null && b.suffix != null) {
            if (!a.suffix.equals(b.suffix)) {
              throw new InterpretingException("Typed operands must have matching types: '" + input + "'");
            }
          } else {
            throw new InterpretingException(
                "Typed operands are not allowed in arithmetic expressions: '" + input + "'");
          }
        }

        // First, evaluate all '*' operators
        for (int k = 0; k < operators.size();) {
          if (operators.get(k) == '*') {
            Operand left = operands.get(k);
            Operand right = operands.get(k + 1);
            int r = left.value * right.value;
            String suffix = left.suffix == null ? right.suffix : left.suffix;
            operands.set(k, new Operand(r, suffix, right.endIndex));
            operands.remove(k + 1);
            operators.remove(k);
            // do not increment k; check at same index again
          } else {
            k++;
          }
        }

        // Then evaluate + and - left-to-right
        int result = operands.get(0).value;
        for (int k = 0; k < operators.size(); k++) {
          char op = operators.get(k);
          Operand right = operands.get(k + 1);
          if (op == '+')
            result = result + right.value;
          else
            result = result - right.value;
          // suffix stays as resultSuffix (both already validated to match or null)
        }

        return Integer.toString(result);
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
