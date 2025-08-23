package magma.util;

import magma.core.CompileException;
import java.util.Map;
import java.util.Set;

public final class ArrayLiteralUtils {
  private ArrayLiteralUtils() {
  }

  public static final class ParseExprResult {
    public final String expr;
    public final int varCount;

    public ParseExprResult(String expr, int varCount) {
      this.expr = expr;
      this.varCount = varCount;
    }
  }

  public static ParseExprResult buildSingleDimArrayLiteral(String[] parts, int varCount, Set<String> letNames,
      Map<String, String> types, Map<String, String> funcAliases) throws CompileException {
    StringBuilder innerOut = new StringBuilder();
    int wrote = 0;
    for (String p : parts) {
      if (p.isEmpty())
        continue;
      magma.parse.ExpressionParser.ParseResult pr = magma.parse.ExpressionParser.parseExprWithLets(p, varCount,
          letNames, types, funcAliases);
      varCount = pr.varCount;
      if (wrote > 0)
        innerOut.append(", ");
      innerOut.append(pr.expr);
      wrote++;
    }
    String expr = "(int[" + wrote + "])" + "{" + innerOut.toString() + "}";
    return new ParseExprResult(expr, varCount);
  }

  public static ParseExprResult buildNestedArrayLiteral(String[] parts, int varCount, Set<String> letNames,
      Map<String, String> types, Map<String, String> funcAliases) throws CompileException {
    java.util.List<String> outerInits = new java.util.ArrayList<>();
    int innerCount = -1;
    for (String p : parts) {
      String part = p.trim();
      if (part.isEmpty())
        continue;
      if (!(part.startsWith("[") && part.endsWith("]")))
        throw new CompileException("Malformed nested array literal: '" + part + "'");
      String innerContent = part.substring(1, part.length() - 1).trim();
      String[] inners = StructParsingUtils.parseCommaSeparatedExpressions(innerContent);
      StringBuilder innerSb = new StringBuilder();
      int wrote = 0;
      for (String ip : inners) {
        if (ip.isEmpty())
          continue;
        magma.parse.ExpressionParser.ParseResult pr = magma.parse.ExpressionParser.parseExprWithLets(ip, varCount,
            letNames, types, funcAliases);
        varCount = pr.varCount;
        if (wrote > 0)
          innerSb.append(", ");
        innerSb.append(pr.expr);
        wrote++;
      }
      if (innerCount == -1)
        innerCount = wrote;
      else if (innerCount != wrote)
        throw new CompileException("Inconsistent inner array sizes in nested array literal");
      outerInits.add("{" + innerSb.toString() + "}");
    }
    int outer = outerInits.size();
    int inner = innerCount == -1 ? 0 : innerCount;
    StringBuilder full = new StringBuilder();
    full.append("(int[").append(outer).append("][").append(inner).append("])").append("{");
    for (int k = 0; k < outerInits.size(); k++) {
      if (k > 0)
        full.append(", ");
      full.append(outerInits.get(k));
    }
    full.append("}");
    return new ParseExprResult(full.toString(), varCount);
  }
}
