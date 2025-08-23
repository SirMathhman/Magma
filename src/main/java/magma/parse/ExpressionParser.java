package magma.parse;

import magma.core.CompileException;
import magma.util.BlockUtils;
import magma.util.ExprUtils;
import magma.util.LiteralUtils;
import magma.util.StructUtils;
import magma.util.IfUtils;
import magma.util.OperatorUtils;

public final class ExpressionParser {
  private ExpressionParser() {
  }

  public static final class ParseResult {
    public final String expr;
    public final int varCount;

    public ParseResult(String expr, int varCount) {
      this.expr = expr;
      this.varCount = varCount;
    }
  }

  public static ParseResult parseExprWithLets(String s, int startVar, java.util.Set<String> letNames,
      java.util.Map<String, String> types, java.util.Map<String, String> funcAliases)
      throws CompileException {
    StringBuilder out = new StringBuilder();
    int idx = 0;
    int len = s.length();
    int varCount = startVar;
    while (idx < len) {
      char c = s.charAt(idx);
      if (Character.isWhitespace(c)) {
        idx++;
        continue;
      }
      int consumedRead = ExprUtils.readIntConsumed(s, idx);
      if (consumedRead > 0) {
        out.append("_v").append(varCount);
        varCount++;
        idx += consumedRead;
        continue;
      }
      int consumedAlias = ExprUtils.aliasCallConsumed(s, idx, funcAliases);
      if (consumedAlias > 0) {
        out.append("_v").append(varCount);
        varCount++;
        idx += consumedAlias;
        continue;
      }
      AtomResult atom = tryHandleAtom(s, idx, out, varCount, letNames, types, funcAliases);
      if (atom != null) {
        varCount = atom.varCount;
        idx = atom.idx;
        continue;
      }
      int consumedInt2 = LiteralUtils.tryAppendLiteral(s, idx, out);
      if (consumedInt2 > 0) {
        idx += consumedInt2;
        continue;
      }
      ExprUtils.OpResult idRes = ExprUtils.handleIdentifierWithLetsResult(s, idx, letNames);
      if (idRes != null) {
        out.append(idRes.out);
        idx = idRes.idx;
        continue;
      }
      int fieldConsumed = StructUtils.handleFieldAccess(s, idx, out, letNames);
      if (fieldConsumed != -1) {
        idx = fieldConsumed;
        continue;
      }
      if (c == '+' || c == '-') {
        out.append(c);
        idx++;
        continue;
      }
      if (c == '.') {
        out.append(c);
        idx++;
        continue;
      }
      throw new CompileException("Unexpected token '" + c + "' at index " + idx + " in expression: '" + s + "'");
    }
    return new ParseResult(out.toString(), varCount);
  }

  private static final class AtomResult {
    final int idx;
    final int varCount;

    AtomResult(int idx, int varCount) {
      this.idx = idx;
      this.varCount = varCount;
    }
  }

  private static AtomResult tryHandleAtom(String s, int idx, StringBuilder out, int varCount,
      java.util.Set<String> letNames, java.util.Map<String, String> types,
      java.util.Map<String, String> funcAliases) throws CompileException {
    char c = s.charAt(idx);
    int opIdx = OperatorUtils.tryHandleLogical(s, idx, out);
    if (opIdx != -1)
      return new AtomResult(opIdx, varCount);
    if (c == '&') {
      ExprUtils.OpResult amp = ExprUtils.handleAmpersandResult(s, idx, letNames);
      out.append(amp.out);
      return new AtomResult(amp.idx, varCount);
    }
    HandleResult ifRes = tryHandleIf(s, idx, out, varCount, letNames, types, funcAliases);
    if (ifRes != null)
      return new AtomResult(ifRes.idx, ifRes.varCount);
    HandleResult blockRes = tryHandleBlock(s, idx, out, varCount, letNames, types, funcAliases);
    if (blockRes != null)
      return new AtomResult(blockRes.idx, blockRes.varCount);
    if (c == '*') {
      ExprUtils.OpResult ast = ExprUtils.handleAsteriskResult(s, idx, letNames, types);
      out.append(ast.out);
      return new AtomResult(ast.idx, varCount);
    }
    int consumedInt2 = LiteralUtils.tryAppendLiteral(s, idx, out);
    if (consumedInt2 > 0)
      return new AtomResult(idx + consumedInt2, varCount);
    ExprUtils.OpResult idRes = ExprUtils.handleIdentifierWithLetsResult(s, idx, letNames);
    if (idRes != null) {
      out.append(idRes.out);
      return new AtomResult(idRes.idx, varCount);
    }
    int fieldConsumed = StructUtils.handleFieldAccess(s, idx, out, letNames);
    if (fieldConsumed != -1)
      return new AtomResult(fieldConsumed, varCount);
    if (c == '+' || c == '-') {
      out.append(c);
      return new AtomResult(idx + 1, varCount);
    }
    if (c == '.') {
      out.append(c);
      return new AtomResult(idx + 1, varCount);
    }
    return null;
  }

  private static final class HandleResult {
    final int idx;
    final int varCount;

    HandleResult(int idx, int varCount) {
      this.idx = idx;
      this.varCount = varCount;
    }
  }

  private static HandleResult tryHandleIf(String s, int idx, StringBuilder out, int varCount,
      java.util.Set<String> letNames, java.util.Map<String, String> types,
      java.util.Map<String, String> funcAliases) throws CompileException {
    IfUtils.IfParts parts = IfUtils.tryFindIfParts(s, idx);
    if (parts == null)
      return null;
    String cond = s.substring(parts.condStart, parts.condEnd).trim();
    ParseResult condPr = parseExprWithLets(cond, varCount, letNames, types, funcAliases);
    int newVar = condPr.varCount;
    String thenInner = s.substring(parts.thenStart + 1, parts.thenEnd - 1).trim();
    ParseResult thenPr = parseExprWithLets(thenInner, newVar, letNames, types, funcAliases);
    newVar = thenPr.varCount;
    String elseInner = s.substring(parts.elseStart + 1, parts.elseEnd - 1).trim();
    ParseResult elsePr = parseExprWithLets(elseInner, newVar, letNames, types, funcAliases);
    newVar = elsePr.varCount;
    out.append("(").append(condPr.expr).append(" ? ").append(thenPr.expr).append(" : ").append(elsePr.expr)
        .append(")");
    return new HandleResult(parts.elseEnd, newVar);
  }

  private static HandleResult tryHandleBlock(String s, int idx, StringBuilder out, int varCount,
      java.util.Set<String> letNames, java.util.Map<String, String> types,
      java.util.Map<String, String> funcAliases) throws CompileException {
    if (s.charAt(idx) != '{')
      return null;
    int j = BlockUtils.findClosingBrace(s, idx);
    if (j == -1)
      throw new CompileException("Unterminated block starting at index " + idx + " in expression: '" + s + "'");
    String inner = s.substring(idx + 1, j - 1).trim();
    ParseResult innerPr = parseExprWithLets(inner, varCount, letNames, types, funcAliases);
    out.append("(").append(innerPr.expr).append(")");
    return new HandleResult(j, innerPr.varCount);
  }
}
