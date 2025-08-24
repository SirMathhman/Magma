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
    final boolean isBool;
    final boolean boolValue;

    Operand(int value, String suffix, int endIndex) {
      this.value = value;
      this.suffix = suffix;
      this.endIndex = endIndex;
      this.isBool = false;
      this.boolValue = false;
    }

    Operand(boolean boolValue, int endIndex) {
      this.value = boolValue ? 1 : 0;
      this.suffix = null;
      this.endIndex = endIndex;
      this.isBool = true;
      this.boolValue = boolValue;
    }
  }

  // Stored variable value (either numeric or boolean)
  private static final class Value {
    final boolean isBool;
    final boolean boolValue;
    final int intValue;

    Value(int v) {
      this.isBool = false;
      this.boolValue = false;
      this.intValue = v;
    }

    Value(boolean b) {
      this.isBool = true;
      this.boolValue = b;
      this.intValue = b ? 1 : 0;
    }
  }

  // Parse an operand (number with optional suffix) starting at index startIdx in
  // s. Variables from `vars` are supported as identifiers.
  // Returns an Operand containing parsed integer, suffix, and end index
  // (position after operand).
  private Operand parseOperand(String s, int startIdx, Map<String, Value> vars, java.util.Set<String> mutables) {
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
      Operand innerOp = evaluateExpression(inner.trim(), vars, mutables);
      if (innerOp.isBool) {
        return new Operand(innerOp.boolValue, j);
      } else {
        return new Operand(innerOp.value, innerOp.suffix, j);
      }
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

    // identifier (variable) or boolean literal
    if (i < len && Character.isLetter(s.charAt(i))) {
      int startId = i;
      i++;
      while (i < len && (Character.isLetterOrDigit(s.charAt(i)) || s.charAt(i) == '_'))
        i++;
      String name = s.substring(startId, i);
      if (name.equals("true")) {
        return new Operand(true, i);
      }
      if (name.equals("false")) {
        return new Operand(false, i);
      }
      Value v = vars.get(name);
      if (v == null) {
        throw new InterpretingException("Unknown variable: '" + name + "'");
      }
      if (v.isBool) {
        return new Operand(v.boolValue, i);
      }
      return new Operand(v.intValue, null, i);
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
    return interpretInternal(input, new HashMap<String, Value>());
  }

  // Internal interpret that accepts a variable map for let-statements and shared
  // evaluation across semicolon-separated statements.
  private String interpretInternal(String input, Map<String, Value> vars) {
    if (input == null)
      return null;
    String trimmed = input.trim();
    // Split by semicolons into statements
    String[] parts = trimmed.split(";");
    int lastValue = 0;
    String lastResultString = null; // when non-null, use this string as the final result (for booleans)
    java.util.Set<String> mutables = new java.util.HashSet<>();
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
              // accept Bool as a known annotation as well
              if ("Bool".equals(typeAnno)) {
                ok = true;
              } else {
                throw new InterpretingException("Unknown type annotation: '" + typeAnno + "'");
              }
            }
          }
        }

        boolean isMutable = false;
        if (name.startsWith("mut ")) {
          isMutable = true;
          name = name.substring(4).trim();
        }
        if (name.isEmpty() || !Character.isLetter(name.charAt(0))) {
          throw new InterpretingException("Invalid variable name: '" + name + "'");
        }
        String rhs = rest.substring(eq + 1).trim();
        Operand op = evaluateExpression(rhs, vars, mutables);
        // store variable under normalized name
        if (op.isBool) {
          vars.put(name, new Value(op.boolValue));
        } else {
          vars.put(name, new Value(op.value));
        }
        if (isMutable) {
          mutables.add(name);
        }
        if (op.isBool) {
          lastResultString = op.boolValue ? "true" : "false";
        } else {
          lastValue = op.value;
        }
      } else {
        // If the statement is a bare identifier, handle special literals or
        // variables.
        String s = stmt;
        if (s.equals("true") || s.equals("false")) {
          lastResultString = s;
          continue;
        }
        if (s.matches("[A-Za-z_][A-Za-z0-9_]*")) {
          if (vars.containsKey(s)) {
            Value vv = vars.get(s);
            if (vv.isBool) {
              lastResultString = vv.boolValue ? "true" : "false";
            } else {
              lastValue = vv.intValue;
            }
            continue;
          } else {
            throw new InterpretingException("Unknown variable: '" + s + "'");
          }
        }

        // Assignment statement: <ident> = <expr>
        int eq = s.indexOf('=');
        if (eq > 0) {
          String lhs = s.substring(0, eq).trim();
          String rhs = s.substring(eq + 1).trim();
          if (lhs.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            if (!vars.containsKey(lhs)) {
              throw new InterpretingException("Unknown variable: '" + lhs + "'");
            }
            if (!mutables.contains(lhs)) {
              throw new InterpretingException("Cannot assign to immutable variable: '" + lhs + "'");
            }
            Operand op = evaluateExpression(rhs, vars, mutables);
            if (op.isBool) {
              vars.put(lhs, new Value(op.boolValue));
            } else {
              vars.put(lhs, new Value(op.value));
              lastValue = op.value;
            }
            continue;
          }
        }

        // If the statement looks like an expression (contains operators,
        // parentheses or digits), evaluate it.
        // treat expressions containing arithmetic or logical operators as expressions
        if (s.matches(".*[+\\-\\*()\\d&|].*")) {
          Operand op = evaluateExpression(stmt, vars, mutables);
          if (op.isBool) {
            lastResultString = op.boolValue ? "true" : "false";
          } else {
            lastValue = op.value;
          }
        } else {
          // fallback: unknown statement form -> error
          throw new InterpretingException("Cannot interpret statement: '" + s + "'");
        }
      }
    }
    String result = (lastResultString != null) ? lastResultString : Integer.toString(lastValue);
    // preserve trailing semicolon if original input ended with one
    if (trimmed.endsWith(";"))
      result = result + ";";
    return result;
  }

  // Evaluate a single expression (no semicolons) and return an Operand.
  private Operand evaluateExpression(String input, Map<String, Value> vars, java.util.Set<String> mutables) {
    String trimmed = input.trim();
    try {
      List<Operand> operands = new ArrayList<>();
      List<String> operators = new ArrayList<>();

      int len = trimmed.length();
      int idx = 0;
      operands.add(parseOperand(trimmed, idx, vars, mutables));
      idx = operands.get(0).endIndex;

      while (true) {
        while (idx < len && Character.isWhitespace(trimmed.charAt(idx)))
          idx++;
        if (idx >= len)
          break;
        // parse operator: could be '*', '+', '-', '&&', '||'
        String op = null;
        char c = trimmed.charAt(idx);
        if (c == '*' || c == '+' || c == '-') {
          op = Character.toString(c);
          idx++;
        } else if (c == '&' || c == '|') {
          if (idx + 1 < len && trimmed.charAt(idx + 1) == c) {
            op = trimmed.substring(idx, idx + 2);
            idx += 2;
          } else {
            break; // invalid single & or |
          }
        } else {
          break;
        }
        operators.add(op);
        Operand next = parseOperand(trimmed, idx, vars, mutables);
        operands.add(next);
        idx = next.endIndex;
      }

      if (operators.isEmpty()) {
        // no operators: single operand
        return operands.get(0);
      }

      // Validate suffix rules pairwise for arithmetic operators only
      for (int k = 0; k < operators.size(); k++) {
        String op = operators.get(k);
        if (op.equals("*") || op.equals("+") || op.equals("-")) {
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
      }

      // Operator precedence passes:
      // helpers will be used to reduce duplication via small combine methods

      // Operator passes using a generic processor to avoid duplication
      OpCombiner numeric = (l, r, o, in) -> makeNumericResult(l, r, o, in);
      OpFilter[] numericPasses = new OpFilter[] { op -> op.equals("*"), op -> (op.equals("+") || op.equals("-")) };
      for (OpFilter f : numericPasses) {
        processOperators(operands, operators, f, numeric, input);
      }
      OpCombiner logical = (l, r, o, in) -> makeBooleanResult(l, r, o, in);
      OpFilter[] logicalPasses = new OpFilter[] { op -> op.equals("&&"), op -> op.equals("||") };
      for (OpFilter f : logicalPasses) {
        processOperators(operands, operators, f, logical, input);
      }

      // After all passes, there should be a single operand left
      return operands.get(0);
    } catch (NumberFormatException e) {
      // fall through to suffix stripping / echoing
    }
    // If the input ends with a type-suffix like I8/I16/I32/I64 or U8/U16/U32/U64,
    // strip it and try to parse as integer.
    String stripped = stripTypeSuffix(input);
    try {
      int v = Integer.parseInt(stripped.trim());
      return new Operand(v, null, stripped.length());
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

  // helper to create a numeric result operand from two numeric operands
  private Operand makeNumericResult(Operand left, Operand right, String op, String input) {
    if (left.isBool || right.isBool) {
      throw new InterpretingException("'" + op + "' cannot be applied to boolean operands: '" + input + "'");
    }
    int r;
    if ("*".equals(op))
      r = left.value * right.value;
    else if ("+".equals(op))
      r = left.value + right.value;
    else
      r = left.value - right.value;
    String suffix = left.suffix == null ? right.suffix : left.suffix;
    return new Operand(r, suffix, right.endIndex);
  }

  // helper to create a boolean result operand from two boolean operands
  private Operand makeBooleanResult(Operand left, Operand right, String op, String input) {
    if (!left.isBool || !right.isBool) {
      throw new InterpretingException("'" + op + "' requires boolean operands: '" + input + "'");
    }
    boolean r = "&&".equals(op) ? (left.boolValue && right.boolValue) : (left.boolValue || right.boolValue);
    return new Operand(r, right.endIndex);
  }

  // collapse operands and operators lists at index k replacing left/right with
  // result
  private void collapseAt(List<Operand> operands, List<String> operators, int k, Operand result) {
    operands.set(k, result);
    operands.remove(k + 1);
    operators.remove(k);
  }

  // small functional interfaces to avoid bringing in java.util.function types
  private interface OpFilter {
    boolean test(String op);
  }

  private interface OpCombiner {
    Operand apply(Operand left, Operand right, String op, String input);
  }

  private void processOperators(List<Operand> operands, List<String> operators, OpFilter filter,
      OpCombiner combiner, String input) {
    for (int k = 0; k < operators.size();) {
      String o = operators.get(k);
      if (filter.test(o)) {
        Operand left = operands.get(k);
        Operand right = operands.get(k + 1);
        Operand result = combiner.apply(left, right, o, input);
        collapseAt(operands, operators, k, result);
      } else {
        k++;
      }
    }
  }
}
