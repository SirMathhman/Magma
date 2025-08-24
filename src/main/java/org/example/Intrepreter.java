package org.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Minimal Intrepreter class. */
public class Intrepreter {
  // intentionally minimal implementation

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
    // If the input is a simple binary integer expression like "5 + 7", "5 - 2", or "3 * 4", evaluate it.
    String trimmed = input.trim();
    Pattern p = Pattern.compile("^\\s*([-]?\\d+)\\s*([+\\-*])\\s*([-]?\\d+)\\s*$");
    Matcher m = p.matcher(trimmed);
    if (m.matches()) {
      try {
        int a = Integer.parseInt(m.group(1));
        String op = m.group(2);
        int b = Integer.parseInt(m.group(3));
        int result;
        switch (op) {
          case "+":
            result = a + b;
            break;
          case "-":
            result = a - b;
            break;
          case "*":
            result = a * b;
            break;
          default:
            return input;
        }
        return Integer.toString(result);
      } catch (NumberFormatException e) {
        // fall through to suffix stripping / echoing
      }
    }

    // If the input ends with a type-suffix like I8/I16/I32/I64 or U8/U16/U32/U64,
    // strip it.
    String[] suffixes = { "I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64" };
    for (String s : suffixes) {
      if (input.endsWith(s)) {
        return input.substring(0, input.length() - s.length());
      }
    }

    return input;
  }
}
