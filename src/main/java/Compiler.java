public class Compiler {
  private static final String PRELUDE = "intrinsic fn readInt() : I32; ";

  public static String compile(String input) throws CompileException {
    if (input == null) {
      throw new CompileException("Input is undefined");
    }

    if (isNonNegativeInteger(input)) {
      return handleNumber(input);
    }

    if (input.startsWith(PRELUDE)) {
      return handlePrelude(input);
    }

    throw new CompileException("Undefined symbol: " + input);
  }

  private static boolean isNonNegativeInteger(String s) {
    return s.matches("\\d+");
  }

  private static String handleNumber(String input) {
    String safe = input.replace("\"", "\\\"");
    return "#include <stdio.h>\nint main() { printf(\"%s\", \"" + safe + "\"); return 0; }";
  }

  private static String handlePrelude(String input) throws CompileException {
    String expr = input.substring(PRELUDE.length()).trim();
    if (expr.isEmpty()) {
      throw new CompileException("Undefined symbol: " + input);
    }

    String letProg = tryLetBinding(expr);
    if (letProg != null) {
      return letProg;
    }

    TokenizationResult tr = tokenizeExpression(expr);
    if (tr.operands.isEmpty()) {
      throw new CompileException("Undefined symbol: " + input);
    }

    for (String o : tr.operands) {
      if (!o.equals("readInt()")) {
        throw new CompileException("Undefined symbol: " + input);
      }
    }

    return generateExprProgram(tr);
  }

  private static String tryLetBinding(String expr) {
    java.util.regex.Pattern letPattern = java.util.regex.Pattern
        .compile("^let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*(?:\\:\\s*I32)?\\s*=\\s*readInt\\(\\)\\s*;\\s*\\1\\s*$");
    java.util.regex.Matcher letMatcher = letPattern.matcher(expr);
    if (letMatcher.matches()) {
      String var = letMatcher.group(1);
      return generateLetProgram(var);
    }
    return null;
  }

  private static String generateLetProgram(String var) {
    StringBuilder sbLet = new StringBuilder();
    sbLet.append("#include <stdio.h>\n");
    sbLet.append("#include <ctype.h>\n");
    sbLet.append("int read_int_safe() {\n");
    sbLet.append("  int sign = 1; int val = 0; int c;\n");
    sbLet.append("  while ((c = getchar()) != EOF) { if (c == '-' || (c >= '0' && c <= '9')) break; }\n");
    sbLet.append("  if (c == EOF) return 0;\n");
    sbLet.append("  if (c == '-') { sign = -1; c = getchar(); }\n");
    sbLet.append("  while (c != EOF && c >= '0' && c <= '9') { val = val * 10 + (c - '0'); c = getchar(); }\n");
    sbLet.append("  return sign * val;\n");
    sbLet.append("}\n");
    sbLet.append("int main() {\n");
    sbLet.append("  int ").append(var).append(" = read_int_safe();\n");
    sbLet.append("  return ").append(var).append(";\n");
    sbLet.append("}\n");
    return sbLet.toString();
  }

  private static TokenizationResult tokenizeExpression(String expr) {
    TokenizationResult res = new TokenizationResult();
    StringBuilder cur = new StringBuilder();
    for (int i = 0; i < expr.length(); i++) {
      char c = expr.charAt(i);
      if (c == '+' || c == '-' || c == '*') {
        String token = cur.toString().trim();
        if (!token.isEmpty()) {
          res.operands.add(token);
        }
        res.ops.add(String.valueOf(c));
        cur.setLength(0);
      } else {
        cur.append(c);
      }
    }
    String last = cur.toString().trim();
    if (!last.isEmpty()) {
      res.operands.add(last);
    }
    return res;
  }

  private static String generateExprProgram(TokenizationResult tr) {
    int count = tr.operands.size();
    StringBuilder sb = new StringBuilder();
    sb.append("#include <stdio.h>\n");
    sb.append("#include <ctype.h>\n");
    sb.append("#include <limits.h>\n");
    sb.append("int read_int_safe() {\n");
    sb.append("  int sign = 1; int val = 0; int c;\n");
    sb.append("  // Skip until we find a digit or a sign or EOF\n");
    sb.append("  while ((c = getchar()) != EOF) {\n");
    sb.append("    if (c == '-' || (c >= '0' && c <= '9')) break;\n");
    sb.append("  }\n");
    sb.append("  if (c == EOF) return 0;\n");
    sb.append("  if (c == '-') { sign = -1; c = getchar(); }\n");
    sb.append("  // Read digits\n");
    sb.append("  while (c != EOF && c >= '0' && c <= '9') {\n");
    sb.append("    val = val * 10 + (c - '0');\n");
    sb.append("    c = getchar();\n");
    sb.append("  }\n");
    sb.append("  return sign * val;\n");
    sb.append("}\n");

    sb.append("int main() {\n");
    sb.append("  int vals[").append(count).append("];\n");
    sb.append("  for (int i = 0; i < ").append(count).append("; i++) { vals[i] = read_int_safe(); }\n");
    sb.append("  int res = vals[0];\n");
    for (int i = 1; i <= count - 1; i++) {
      String op = tr.ops.get(i - 1);
      if ("+".equals(op)) {
        sb.append("  res = res + vals[").append(i).append("];\n");
      } else if ("-".equals(op)) {
        sb.append("  res = res - vals[").append(i).append("];\n");
      } else if ("*".equals(op)) {
        sb.append("  res = res * vals[").append(i).append("];\n");
      } else {
        throw new RuntimeException("Unsupported operator: " + op);
      }
    }
    sb.append("  return res;\n");
    sb.append("}\n");
    return sb.toString();
  }

  private static class TokenizationResult {
    java.util.List<String> ops = new java.util.ArrayList<>();
    java.util.List<String> operands = new java.util.ArrayList<>();
  }
}
