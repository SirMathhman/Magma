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

    String letSeqProg = tryLetSequence(expr);
    if (letSeqProg != null) {
      return letSeqProg;
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

  private static String tryLetSequence(String expr) {
    String[] parts = expr.split(";");
    java.util.List<String> lets = new java.util.ArrayList<>();
    int i = 0;
    java.util.regex.Pattern letPattern = java.util.regex.Pattern
        .compile("^let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*(?:\\:\\s*I32)?\\s*=\\s*readInt\\(\\)\\s*$");
    for (; i < parts.length; i++) {
      String part = parts[i].trim();
      if (part.isEmpty()) {
        continue;
      }
      java.util.regex.Matcher m = letPattern.matcher(part);
      if (m.matches()) {
        lets.add(m.group(1));
      } else {
        break;
      }
    }

    if (lets.isEmpty()) {
      return null;
    }

    // Reconstruct remaining expression
    StringBuilder remaining = new StringBuilder();
    for (int j = i; j < parts.length; j++) {
      if (j > i) {
        remaining.append(";");
      }
      remaining.append(parts[j]);
    }
    String remExpr = remaining.toString().trim();
    if (remExpr.isEmpty()) {
      return null;
    }

    TokenizationResult tr = tokenizeExpression(remExpr);
    if (tr.operands.isEmpty()) {
      return null;
    }

    for (String o : tr.operands) {
      if (!o.equals("readInt()") && !lets.contains(o)) {
        return null;
      }
    }

    StringBuilder sb = new StringBuilder();
    // inline read_int_safe function
    sb.append("#include <stdio.h>\n");
    sb.append("#include <ctype.h>\n");
    sb.append("#include <limits.h>\n");
    sb.append("int read_int_safe() {\n");
    sb.append("  int sign = 1; int val = 0; int c;\n");
    sb.append("  while ((c = getchar()) != EOF) { if (c == '-' || (c >= '0' && c <= '9')) break; }\n");
    sb.append("  if (c == EOF) return 0;\n");
    sb.append("  if (c == '-') { sign = -1; c = getchar(); }\n");
    sb.append("  while (c != EOF && c >= '0' && c <= '9') { val = val * 10 + (c - '0'); c = getchar(); }\n");
    sb.append("  return sign * val;\n");
    sb.append("}\n");

    sb.append(generateMainWithLets(lets, tr));
    return sb.toString();
  }

  private static String generateMainWithLets(java.util.List<String> lets, TokenizationResult tr) {
    StringBuilder sb = new StringBuilder();
    sb.append("int main() {\n");
    for (String name : lets) {
      sb.append("  int ").append(name).append(" = read_int_safe();\n");
    }

    int extraReads = 0;
    for (String o : tr.operands) {
      if (o.equals("readInt()")) {
        extraReads++;
      }
    }
    if (extraReads > 0) {
      sb.append("  int vals[").append(extraReads).append("];\n");
      sb.append("  for (int i = 0; i < ").append(extraReads).append("; i++) { vals[i] = read_int_safe(); }\n");
    }

    sb.append("  int res = ");
    int valIndex = 0;
    StringBuilder exprBuilder = new StringBuilder();
    for (int k = 0; k < tr.operands.size(); k++) {
      String operand = tr.operands.get(k);
      if (k > 0) {
        exprBuilder.append(' ').append(tr.ops.get(k - 1)).append(' ');
      }
      if (operand.equals("readInt()")) {
        exprBuilder.append("vals[").append(valIndex).append("]");
        valIndex++;
      } else {
        exprBuilder.append(operand);
      }
    }
    sb.append(exprBuilder.toString()).append(";\n");
    sb.append("  return res;\n");
    sb.append("}\n");
    return sb.toString();
  }
}
