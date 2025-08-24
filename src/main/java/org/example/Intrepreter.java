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
    final boolean isFloat;
    final double floatValue;

    Operand(int value, String suffix, int endIndex) {
      this.value = value;
      this.suffix = suffix;
      this.endIndex = endIndex;
      this.isBool = false;
      this.boolValue = false;
      this.isFloat = false;
      this.floatValue = 0.0;
    }

    Operand(boolean boolValue, int endIndex) {
      this.value = boolValue ? 1 : 0;
      this.suffix = null;
      this.endIndex = endIndex;
      this.isBool = true;
      this.boolValue = boolValue;
      this.isFloat = false;
      this.floatValue = 0.0;
    }

    Operand(double floatValue, int endIndex) {
      this.value = (int) floatValue;
      this.suffix = null;
      this.endIndex = endIndex;
      this.isBool = false;
      this.boolValue = false;
      this.isFloat = true;
      this.floatValue = floatValue;
    }
  }

  // Stored variable value (either numeric or boolean)
  private static final class Value {
    final boolean isBool;
    final boolean boolValue;
    final int intValue;
    final boolean isFloat;
    final double floatValue;

    Value(int v) {
      this.isBool = false;
      this.boolValue = false;
      this.intValue = v;
      this.isFloat = false;
      this.floatValue = 0.0;
    }

    Value(boolean b) {
      this.isBool = true;
      this.boolValue = b;
      this.intValue = b ? 1 : 0;
      this.isFloat = false;
      this.floatValue = 0.0;
    }

    Value(double f) {
      this.isBool = false;
      this.boolValue = false;
      this.intValue = (int) f;
      this.isFloat = true;
      this.floatValue = f;
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
      } else if (innerOp.isFloat) {
        return new Operand(innerOp.floatValue, j);
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
    // read digits (and optional fractional part) or identifier
    int startDigits = i;
    while (i < len && Character.isDigit(s.charAt(i)))
      i++;
    boolean hasIntDigits = i > startDigits;
    boolean hasDot = false;
    int fracStart = -1;
    if (i < len && s.charAt(i) == '.') {
      hasDot = true;
      i++;
      fracStart = i;
      while (i < len && Character.isDigit(s.charAt(i)))
        i++;
    }
    if (hasIntDigits || (hasDot && i > fracStart)) {
      String numToken = s.substring(startDigits, i);
      if (hasDot) {
        double d = Double.parseDouble((sign < 0 ? "-" : "") + numToken);
        return new Operand(d, i);
      } else {
        int value = Integer.parseInt(numToken) * sign;
        // read possible integer suffix
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
      } else if (v.isFloat) {
        return new Operand(v.floatValue, i);
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
            if (!ok && "Bool".equals(typeAnno))
              ok = true;
            if (!ok && isFloatType(typeAnno))
              ok = true;
            if (!ok) {
              throw new InterpretingException("Unknown type annotation: '" + typeAnno + "'");
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
        // store variable under normalized name (respect F32 annotation)
        if (isFloatType(typeAnno)) {
          double f = op.isFloat ? op.floatValue : (double) op.value;
          vars.put(name, new Value(f));
        } else if (op.isBool) {
          vars.put(name, new Value(op.boolValue));
        } else if (op.isFloat) {
          vars.put(name, new Value(op.floatValue));
        } else {
          vars.put(name, new Value(op.value));
        }
        if (isMutable) {
          mutables.add(name);
        }
        if (op.isBool) {
          lastResultString = op.boolValue ? "true" : "false";
        } else if (op.isFloat || isFloatType(typeAnno)) {
          double f = op.isFloat ? op.floatValue : (double) op.value;
          lastResultString = Double.toString(f);
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
            } else if (vv.isFloat) {
              lastResultString = Double.toString(vv.floatValue);
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
            Value current = vars.get(lhs);
            if (op.isBool) {
              vars.put(lhs, new Value(op.boolValue));
              lastResultString = op.boolValue ? "true" : "false";
            } else if (op.isFloat || (current != null && current.isFloat)) {
              double f = op.isFloat ? op.floatValue : (double) op.value;
              vars.put(lhs, new Value(f));
              lastResultString = Double.toString(f);
            } else {
              vars.put(lhs, new Value(op.value));
              lastValue = op.value;
            }
            continue;
          }
        }

        // If the statement looks like an expression (contains operators,
        // parentheses, digits or decimal point), evaluate it.
        // treat expressions containing arithmetic or logical operators as expressions
        if (s.matches(".*[+\\-\\*()\\d&|\\.].*")) {
          Operand op = evaluateExpression(stmt, vars, mutables);
          if (op.isBool) {
            lastResultString = op.boolValue ? "true" : "false";
          } else if (op.isFloat) {
            lastResultString = Double.toString(op.floatValue);
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
        // parse operator: '*', '+', '-', logical '&&', '||', or comparisons '==', '!=',
        // '<', '<=', '>', '>='
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
        } else if (c == '=' && idx + 1 < len && trimmed.charAt(idx + 1) == '=') {
          op = "==";
          idx += 2;
        } else if (c == '!' && idx + 1 < len && trimmed.charAt(idx + 1) == '=') {
          op = "!=";
          idx += 2;
        } else if (c == '<' || c == '>') {
          if (idx + 1 < len && trimmed.charAt(idx + 1) == '=') {
            op = trimmed.substring(idx, idx + 2); // <= or >=
            idx += 2;
          } else {
            op = Character.toString(c); // < or >
            idx++;
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
      // Float arithmetic supported below via numeric combiner

      // Validate suffix rules pairwise for arithmetic operators only
      for (int k = 0; k < operators.size(); k++) {
        String op = operators.get(k);
        if (op.equals("*") || op.equals("+") || op.equals("-")) {
          Operand a = operands.get(k);
          Operand b = operands.get(k + 1);
          enforceIntegerSuffixCompatibility(a, b, input);
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
      // 5) comparisons: all produce boolean. Enforce same-type operands.
      OpCombiner cmp = (l, r, o, in) -> makeComparisonResult(l, r, o, in);
      OpFilter[] cmpPasses = new OpFilter[] {
          op -> op.equals("=="), op -> op.equals("!="),
          op -> op.equals("<"), op -> op.equals("<="),
          op -> op.equals(">"), op -> op.equals(">=")
      };
      for (OpFilter f : cmpPasses) {
        processOperators(operands, operators, f, cmp, input);
      }

      // 4) logical: && then ||
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
    // If either is float, compute in double and return float operand
    if (left.isFloat || right.isFloat) {
      double lv = left.isFloat ? left.floatValue : left.value;
      double rv = right.isFloat ? right.floatValue : right.value;
      double r;
      if ("*".equals(op))
        r = lv * rv;
      else if ("+".equals(op))
        r = lv + rv;
      else
        r = lv - rv;
      return new Operand(r, right.endIndex);
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

  // helper for comparisons; enforce same-type numeric operands and return boolean
  private Operand makeComparisonResult(Operand left, Operand right, String op, String input) {
    if (left.isBool || right.isBool) {
      throw new InterpretingException("Comparison requires numeric operands: '" + input + "'");
    }
    // same-type enforcement: both float or both integers with matching suffix rules
    boolean leftFloat = left.isFloat;
    boolean rightFloat = right.isFloat;
    if (leftFloat != rightFloat) {
      throw new InterpretingException("Comparison operands must be the same type: '" + input + "'");
    }
    if (!leftFloat) {
      enforceIntegerSuffixCompatibility(left, right, input);
      int lv = left.value;
      int rv = right.value;
      int cmp = Integer.compare(lv, rv);
      boolean r = applyComparisonFromCmp(cmp, op, input);
      return new Operand(r, right.endIndex);
    } else {
      double lv = left.floatValue;
      double rv = right.floatValue;
      int cmp = Double.compare(lv, rv);
      boolean r = applyComparisonFromCmp(cmp, op, input);
      return new Operand(r, right.endIndex);
    }
  }

  // shared integer suffix compatibility enforcement
  private void enforceIntegerSuffixCompatibility(Operand a, Operand b, String input) {
    if (a.suffix == null && b.suffix == null) {
      return;
    } else if (a.suffix != null && b.suffix != null) {
      if (!a.suffix.equals(b.suffix)) {
        throw new InterpretingException("Typed operands must have matching types: '" + input + "'");
      }
    } else {
      throw new InterpretingException(
          "Typed operands are not allowed in arithmetic expressions: '" + input + "'");
    }
  }

  // map a generic comparison result (-1,0,1) to boolean by operator
  private boolean applyComparisonFromCmp(int cmp, String op, String input) {
    switch (op) {
      case "==":
        return cmp == 0;
      case "!=":
        return cmp != 0;
      case "<":
        return cmp < 0;
      case "<=":
        return cmp <= 0;
      case ">":
        return cmp > 0;
      case ">=":
        return cmp >= 0;
      default:
        throw new InterpretingException("Unknown comparison operator in '" + input + "'");
    }
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

  private boolean isFloatType(String typeAnno) {
    return "F32".equals(typeAnno) || "F64".equals(typeAnno);
  }
}
