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

    // No let bindings: build program from a synthetic ParseResult
    ParseResult pr = new ParseResult();
    // fill readSequence with nulls for each readInt() occurrence
    for (String o : tr.operands) {
      if (o.equals("readInt()")) {
        pr.readSequence.add(null);
      }
    }
    return buildProgramFromParseResult(pr, tr);
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

  // generateExprProgram removed; logic moved into buildProgramFromParseResult

  private static class TokenizationResult {
    java.util.List<String> ops = new java.util.ArrayList<>();
    java.util.List<String> operands = new java.util.ArrayList<>();
  }

  private static String tryLetSequence(String expr) {
    String[] parts = expr.split(";");
    ParseResult pr = parseLetSequenceParts(parts);
    if (pr == null || pr.declared.isEmpty()) {
      return null;
    }

    String remExpr = pr.remaining.trim();
    if (remExpr.isEmpty()) {
      return null;
    }

    TokenizationResult tr = tokenizeExpression(remExpr);
    if (tr.operands.isEmpty()) {
      return null;
    }

    for (String o : tr.operands) {
      if (!o.equals("readInt()") && !pr.declared.containsKey(o)) {
        return null;
      }
    }

    // Append readInt() occurrences in expression to the read sequence
    for (String o : tr.operands) {
      if (o.equals("readInt()")) {
        pr.readSequence.add(null);
      }
    }

    return buildProgramFromParseResult(pr, tr);
  }

  private static class ParseResult {
    java.util.Map<String, Boolean> declared = new java.util.LinkedHashMap<>();
    java.util.List<String> readSequence = new java.util.ArrayList<>();
    String remaining = "";
  }

  private static ParseResult parseLetSequenceParts(String[] parts) {
    ParseResult pr = new ParseResult();
    int i = 0;
    java.util.regex.Pattern letReadPattern = java.util.regex.Pattern
        .compile("^let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*(?:\\:\\s*I32)?\\s*=\\s*readInt\\(\\)\\s*$");
    java.util.regex.Pattern letMutZeroPattern = java.util.regex.Pattern
        .compile("^let\\s+mut\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*(?:\\:\\s*I32)?\\s*=\\s*0\\s*$");
    java.util.regex.Pattern assignReadPattern = java.util.regex.Pattern
        .compile("^([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*readInt\\(\\)\\s*$");

    for (; i < parts.length; i++) {
      String part = parts[i].trim();
      if (part.isEmpty()) {
        continue;
      }
      java.util.regex.Matcher m1 = letReadPattern.matcher(part);
      java.util.regex.Matcher m2 = letMutZeroPattern.matcher(part);
      java.util.regex.Matcher m3 = assignReadPattern.matcher(part);
      if (m1.matches()) {
        String name = m1.group(1);
        pr.declared.put(name, Boolean.FALSE);
        pr.readSequence.add(name);
      } else if (m2.matches()) {
        String name = m2.group(1);
        pr.declared.put(name, Boolean.TRUE);
      } else if (m3.matches()) {
        String name = m3.group(1);
        if (pr.declared.containsKey(name) && pr.declared.get(name)) {
          pr.readSequence.add(name);
        } else {
          break;
        }
      } else {
        break;
      }
    }

    StringBuilder remaining = new StringBuilder();
    for (int j = i; j < parts.length; j++) {
      if (j > i) {
        remaining.append(";");
      }
      remaining.append(parts[j]);
    }
    pr.remaining = remaining.toString();
    return pr;
  }

  private static String buildProgramFromParseResult(ParseResult pr, TokenizationResult tr) {
    // Build C program preserving read order based on ParseResult and tokenized
    // expression
    StringBuilder sb = new StringBuilder();
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

    sb.append("int main() {\n");
    // declare variables
    for (String name : pr.declared.keySet()) {
      sb.append("  int ").append(name).append(" = 0;\n");
    }

    // prepare vals array for expression-only reads
    int exprReadCount = 0;
    for (String o : tr.operands) {
      if (o.equals("readInt()")) {
        exprReadCount++;
      }
    }
    if (exprReadCount > 0) {
      sb.append("  int vals[").append(exprReadCount).append("];\n");
    }

    // emit reads in order
    int valIdx = 0;
    for (String target : pr.readSequence) {
      if (target == null) {
        sb.append("  vals[").append(valIdx).append("] = read_int_safe();\n");
        valIdx++;
      } else {
        sb.append("  ").append(target).append(" = read_int_safe();\n");
      }
    }

    // build expression using vars and vals
    sb.append("  int res = ");
    int exprValCounter = 0;
    StringBuilder exprBuilder = new StringBuilder();
    for (int k = 0; k < tr.operands.size(); k++) {
      String operand = tr.operands.get(k);
      if (k > 0) {
        exprBuilder.append(' ').append(tr.ops.get(k - 1)).append(' ');
      }
      if (operand.equals("readInt()")) {
        exprBuilder.append("vals[").append(exprValCounter++).append("]");
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
