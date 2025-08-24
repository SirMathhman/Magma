package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
  // s. Variables from `vars` are supported as identifiers.
  // Returns an Operand containing parsed integer, suffix, and end index
  // (position after operand).
  private Operand parseOperand(String s, int startIdx, Map<String, Integer> vars) {
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
      int val = evaluateExpression(inner.trim(), vars);
      return new Operand(val, null, j);
    }

    // optional leading sign for number
    int sign = 1;
    if (i < len && s.charAt(i) == '+') {
      i++;
    } else if (i < len && s.charAt(i) == '-') {
      sign = -1;
      i++;
    }
    // read digits or identifier
    int startDigits = i;
    while (i < len && Character.isDigit(s.charAt(i)))
      i++;
    if (startDigits != i) {
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

    // identifier (variable)
    if (i < len && Character.isLetter(s.charAt(i))) {
      int startId = i;
      i++;
      while (i < len && (Character.isLetterOrDigit(s.charAt(i)) || s.charAt(i) == '_'))
        i++;
      String name = s.substring(startId, i);
      Integer v = vars.get(name);
      if (v == null) {
        throw new InterpretingException("Unknown variable: '" + name + "'");
      }
      return new Operand(v, null, i);
    }

    throw new NumberFormatException("Expected number or identifier at index " + startIdx);
  }

  /**
   * Interpret the given input string and return a result string.
   * Current implementation echoes the input.
   *
   * @param input the input string to interpret
   * @return the interpreted string (echoes input)
   */
  public String interpret(String input) {
    return interpretInternal(input, new HashMap<>());
  }

  // Internal interpret that accepts a variable map for let-statements and shared
  // evaluation across semicolon-separated statements.
  private String interpretInternal(String input, Map<String, Integer> vars) {
    if (input == null)
      return null;
    String trimmed = input.trim();
    // Split by semicolons into statements
    String[] parts = trimmed.split(";");
    int lastValue = 0;
    for (int p = 0; p < parts.length; p++) {
      String stmt = parts[p].trim();
      if (stmt.isEmpty())
        continue;
      if (stmt.startsWith("let ")) {
        // let <name> = <expr>
        String rest = stmt.substring(4).trim();
        int eq = rest.indexOf('=');
        if (eq <= 0) {
          throw new InterpretingException("Invalid let statement: '" + stmt + "'");
        }
        String namePart = rest.substring(0, eq).trim();
        // support optional type annotation: name : TYPE
        String name = namePart;
        String typeAnno = null;
        int colon = namePart.indexOf(':');
        if (colon >= 0) {
          name = namePart.substring(0, colon).trim();
          typeAnno = namePart.substring(colon + 1).trim();
          // normalize common formatting like I32 -> I32 (keep as-is)
          if (typeAnno.isEmpty())
            typeAnno = null;
          else {
            // validate annotation if present
            boolean ok = false;
            for (String sfx : TYPE_SUFFIXES) {
              if (sfx.equals(typeAnno)) {
                ok = true;
                break;
              }
            }
            if (!ok) {
              throw new InterpretingException("Unknown type annotation: '" + typeAnno + "'");
            }
          }
        }

        if (name.isEmpty() || !Character.isLetter(name.charAt(0))) {
          throw new InterpretingException("Invalid variable name: '" + name + "'");
        }
        String rhs = rest.substring(eq + 1).trim();
        int v = evaluateExpression(rhs, vars);
        // store variable (we ignore annotation at runtime for now)
        vars.put(name, v);
        lastValue = v;
      } else {
        // If the statement is a bare identifier, return its value if present or
        // echo the identifier otherwise.
        String s = stmt;
        if (s.matches("[A-Za-z_][A-Za-z0-9_]*")) {
          if (vars.containsKey(s)) {
            lastValue = vars.get(s);
            continue;
          } else {
            // unknown bare word, echo (strip suffixes if any)
            return stripTypeSuffix(s);
          }
        }

        // If the statement looks like an expression (contains operators,
        // parentheses or digits), evaluate it.
        if (s.matches(".*[+\\-\\*()\\d].*")) {
          int v = evaluateExpression(stmt, vars);
          lastValue = v;
        } else {
          // fallback: echo the statement (strip possible type suffix)
          return stripTypeSuffix(s);
        }
      }
    }
    String result = Integer.toString(lastValue);
    // preserve trailing semicolon if original input ended with one
    if (trimmed.endsWith(";"))
      result = result + ";";
    return result;
  }

  // Evaluate a single expression (no semicolons) and return integer result.
  private int evaluateExpression(String input, Map<String, Integer> vars) {
    String trimmed = input.trim();
    try {
      List<Operand> operands = new ArrayList<>();
      List<Character> operators = new ArrayList<>();

      int len = trimmed.length();
      int idx = 0;
      operands.add(parseOperand(trimmed, idx, vars));
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
        Operand next = parseOperand(trimmed, idx, vars);
        operands.add(next);
        idx = next.endIndex;
      }

      if (operators.isEmpty()) {
        // no operators: single operand
        return operands.get(0).value;
      }

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
      }

      return result;
    } catch (NumberFormatException e) {
      // fall through to suffix stripping / echoing
    }
    // If the input ends with a type-suffix like I8/I16/I32/I64 or U8/U16/U32/U64,
    // strip it and try to parse as integer.
    String stripped = stripTypeSuffix(input);
    try {
      return Integer.parseInt(stripped.trim());
    } catch (NumberFormatException ex) {
      throw new InterpretingException("Cannot evaluate expression: '" + input + "'");
    }
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
