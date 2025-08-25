package org.example;

/** Minimal Intrepreter class. */
public class Intrepreter {
  public String interpret(String input) throws InterpretingException {
    if (input == null || input.isEmpty()) {
      throw new InterpretingException("Undefined value", String.valueOf(input));
    }

    // 1) Fast path: plain decimal integer => echo back exactly.
    if (isAllDigits(input)) {
      return input;
    }

    // 2) Minimal language: "let <id> = <int>; <expr>" where <expr> is <id> or
    // <int>.
    int i = 0;
    final int n = input.length();

    i = skipSpaces(input, i);

    if (startsWithLet(input, i)) {
      i += 3; // consume 'let'
      // require at least one space after 'let'
      if (i >= n || !isSpace(input.charAt(i))) {
        throw new InterpretingException("Undefined value", input);
      }
      i = skipSpaces(input, i);

      // identifier
      int idStart = i;
      String ident = parseIdentifier(input, i);
      if (ident == null) {
        throw new InterpretingException("Undefined value", input);
      }
      i = idStart + ident.length();

      // spaces and '='
      i = skipSpaces(input, i);
      i = expectCharOrThrow(input, i, '=');
      i = skipSpaces(input, i);

      // integer literal
      int intStart = i;
      String intLit = parseInteger(input, i);
      if (intLit == null) {
        throw new InterpretingException("Undefined value", input);
      }
      i = intStart + intLit.length();

      // spaces
      i = skipSpaces(input, i);

      // ';' and spaces
      i = expectCharOrThrow(input, i, ';');
      i = skipSpaces(input, i);

      // expression: either identifier or integer
      String result;
      if (i < n && isIdentStart(input.charAt(i))) {
        String ref = parseIdentifier(input, i);
        if (ref == null) {
          throw new InterpretingException("Undefined value", input);
        }
        i += ref.length();
        if (!ref.equals(ident)) {
          // only single-binding supported
          throw new InterpretingException("Undefined value", ref);
        }
        result = intLit;
      } else {
        String rhsInt = parseInteger(input, i);
        if (rhsInt == null) {
          throw new InterpretingException("Undefined value", input);
        }
        i += rhsInt.length();
        result = rhsInt;
      }

      // trailing spaces
      i = skipSpaces(input, i);
      if (i != n) {
        // unexpected trailing content
        throw new InterpretingException("Undefined value", input.substring(i));
      }
      return result;
    }

    // Anything else is currently undefined.
    throw new InterpretingException("Undefined value", input);
  }

  private static boolean isAllDigits(String s) {
    if (s.isEmpty())
      return false;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c < '0' || c > '9')
        return false;
    }
    return true;
  }

  private static boolean startsWithLet(String s, int i) {
    int n = s.length();
    return i + 2 < n && s.charAt(i) == 'l' && s.charAt(i + 1) == 'e' && s.charAt(i + 2) == 't';
  }

  private static boolean isSpace(char c) {
    return c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f';
  }

  private static int skipSpaces(String s, int i) {
    final int n = s.length();
    while (i < n && isSpace(s.charAt(i)))
      i++;
    return i;
  }

  private static boolean isIdentStart(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
  }

  private static boolean isIdentPart(char c) {
    return isIdentStart(c) || (c >= '0' && c <= '9');
  }

  private static String parseIdentifier(String s, int i) {
    int n = s.length();
    if (i >= n || !isIdentStart(s.charAt(i)))
      return null;
    int j = i + 1;
    while (j < n && isIdentPart(s.charAt(j)))
      j++;
    return s.substring(i, j);
  }

  private static String parseInteger(String s, int i) {
    int n = s.length();
    if (i >= n)
      return null;
    int j = i;
    while (j < n) {
      char c = s.charAt(j);
      if (c < '0' || c > '9')
        break;
      j++;
    }
    if (j == i)
      return null; // no digits
    return s.substring(i, j);
  }

  private static int expectCharOrThrow(String input, int i, char expected) {
    if (i >= input.length() || input.charAt(i) != expected) {
      throw new InterpretingException("Undefined value", input);
    }
    return i + 1;
  }
}
