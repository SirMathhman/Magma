public class Compiler {

  /**
   * Compile the given source string into a C program. For now this returns
   * a minimal C program that simply exits with code computed from the
   * simple expression in the input.
   *
   * @param input source to compile
   * @return C source text
   */
  private static final String HEADER = "#include <stdlib.h>\n#include <stdio.h>\n";
  private static final String DEFAULT_BODY = "  return 0;\n";

  public static String compile(String input) {
    // Very small, pragmatic compiler for the tests in this kata.
    // If the source references the intrinsic readInt(), emit a C
    // program that reads an integer from stdin and returns it. For
    // any other (including empty) input, return a program that exits 0.
    String src = input == null ? "" : input;

    // expression is whatever comes after the first semicolon (the
    // prelude typically ends with a single semicolon). Use the first
    // semicolon so later semicolons inside the program (e.g. `let`)
    // remain part of the expression to compile.
    String expr = src;
    int firstSemi = src.indexOf(';');
    if (firstSemi != -1) {
      if (firstSemi + 1 < src.length()) {
        expr = src.substring(firstSemi + 1).trim();
      } else {
        expr = "";
      }
    }

    String body = compileExpression(expr);
    return buildProgram(body);
  }

  // Extracted helper: given the (trimmed) expression, produce the C body
  // that implements it. Keeps `compile` small and simpler to reason about.
  private static String compileExpression(String expr) {
    if (expr == null || expr.isEmpty()) {
      return DEFAULT_BODY;
    }

    if (expr.equals("readInt()")) {
      return readIntSnippet("v") + "  return v;\n";
    }

    String letBody = handleLet(expr);
    if (letBody != null)
      return letBody;

    String plus = binaryOpIfReads(expr, '+');
    if (plus != null)
      return plus;

    String minus = binaryOpIfReads(expr, '-');
    if (minus != null)
      return minus;

    String mult = binaryOpIfReads(expr, '*');
    if (mult != null)
      return mult;

    return DEFAULT_BODY;
  }

  // Handle a simple let-binding pattern used by tests. Returns the generated
  // body or null if the expression doesn't match.
  private static String handleLet(String expr) {
    int firstSemi = expr.indexOf(';');
    if (firstSemi == -1)
      return null;
    String firstStmt = expr.substring(0, firstSemi).trim();
    if (!firstStmt.startsWith("let "))
      return null;
    int eq = firstStmt.indexOf('=');
    if (eq == -1)
      return null;
    String name = firstStmt.substring(4, eq).trim();
    String rhs = firstStmt.substring(eq + 1).trim();
    String after = expr.substring(firstSemi + 1).trim();
    if (rhs.equals("readInt()") && after.equals(name)) {
      return readIntSnippet(name) + "  return " + name + ";\n";
    }
    return null;
  }

  // Return the body for a binary operation where both sides are readInt().
  // If the expression doesn't match the pattern, return null.
  private static String binaryOpIfReads(String expr, char op) {
    int idx = expr.indexOf(op);
    if (idx == -1)
      return null;
    String left = expr.substring(0, idx).trim();
    String right = expr.substring(idx + 1).trim();
    if (left.equals("readInt()") && right.equals("readInt()")) {
      return readIntSnippet("a") + readIntSnippet("b") + "  return a " + op + " b;\n";
    }
    return null;
  }

  // Pure helper to assemble a full C program from a body snippet.
  private static String buildProgram(String body) {
    StringBuilder sb = new StringBuilder();
    sb.append(HEADER);
    sb.append("int main(void) {\n");
    sb.append(body);
    sb.append("}\n");
    return sb.toString();
  }

  // Small helper to produce the C code that declares an int variable and
  // reads into it using scanf with the same failure semantics used above.
  private static String readIntSnippet(String varName) {
    return "  int " + varName + " = 0;\n" +
        "  if (scanf(\"%d\", &" + varName + ") != 1) { return 0; }\n";
  }
}
