package magma.parse;

import magma.core.CompileException;
import magma.util.BlockUtils;
import magma.util.ExprUtils;
import java.util.Optional;
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
        // emit a runtime read call so input is only consumed when the branch
        // containing it is executed
        out.append("read_input()");
        idx += consumedRead;
        continue;
      }
  int consumedAlias = ExprUtils.aliasCallConsumed(s, idx, Optional.ofNullable(funcAliases));
      if (consumedAlias > 0) {
        out.append("_v").append(varCount);
        varCount++;
        idx += consumedAlias;
        continue;
      }
      AtomResult atom = tryHandleAtom(s, idx, out, varCount, letNames, types, funcAliases);
      if (atom != null && atom.idx > idx) {
        varCount = atom.varCount;
        idx = atom.idx;
        continue;
      }
      int consumedInt2 = LiteralUtils.tryAppendLiteral(s, idx, out);
      if (consumedInt2 > 0) {
        idx += consumedInt2;
        continue;
      }
      Optional<ExprUtils.OpResult> idResOpt = ExprUtils.handleIdentifierWithLetsResult(s, idx, Optional.ofNullable(letNames));
      if (idResOpt.isPresent()) {
        ExprUtils.OpResult idRes = idResOpt.get();
        out.append(idRes.out);
        idx = idRes.idx;
        continue;
      }
      int fieldConsumed = StructUtils.handleFieldAccess(s, idx, out, Optional.ofNullable(letNames));
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
      ExprUtils.OpResult amp = ExprUtils.handleAmpersandResult(s, idx, Optional.ofNullable(letNames));
      out.append(amp.out);
      return new AtomResult(amp.idx, varCount);
    }
    Optional<HandleResult> ifRes = tryHandleIf(s, idx, out, varCount, letNames, types, funcAliases);
    if (ifRes.isPresent())
      return new AtomResult(ifRes.get().idx, ifRes.get().varCount);
    Optional<HandleResult> blockRes = tryHandleBlock(s, idx, out, varCount, letNames, types, funcAliases);
    if (blockRes.isPresent())
      return new AtomResult(blockRes.get().idx, blockRes.get().varCount);
    if (c == '*') {
      ExprUtils.OpResult ast = ExprUtils.handleAsteriskResult(s, idx, Optional.ofNullable(letNames), Optional.ofNullable(types));
      out.append(ast.out);
      return new AtomResult(ast.idx, varCount);
    }
    int consumedInt2 = LiteralUtils.tryAppendLiteral(s, idx, out);
    if (consumedInt2 > 0)
      return new AtomResult(idx + consumedInt2, varCount);
    AtomResult idAtom = tryHandleIdentifierOrPostInc(s, idx, out, varCount, letNames);
    if (idAtom != null) {
      return idAtom;
    }
    int fieldConsumed = StructUtils.handleFieldAccess(s, idx, out, Optional.ofNullable(letNames));
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
    return Optional.<AtomResult>empty().orElse(new AtomResult(idx, varCount));
  }

  private static final class HandleResult {
    final int idx;
    final int varCount;

    HandleResult(int idx, int varCount) {
      this.idx = idx;
      this.varCount = varCount;
    }
  }

  private static AtomResult tryHandleIdentifierOrPostInc(String s, int idx, StringBuilder out, int varCount,
      java.util.Set<String> letNames) throws CompileException {
    ExprUtils.IdentResult identRes = ExprUtils.collectIdentifierResult(s, idx);
    if (identRes.ident.isEmpty())
      return null;
    String name = identRes.ident;
    int afterIdent = identRes.idx;
    // post-increment
    if (afterIdent + 1 < s.length() && s.charAt(afterIdent) == '+' && s.charAt(afterIdent + 1) == '+') {
      if (!letNames.contains(name))
        throw new CompileException("Invalid post-increment of non-mut variable " + name);
      String letRef = "let_" + name;
      String tmp = "_v" + varCount;
      out.append("(").append(tmp).append(" = ").append(letRef).append(", ").append(letRef).append(" = ")
          .append(letRef).append(" + 1, ").append(tmp).append(")");
      return new AtomResult(afterIdent + 2, varCount + 1);
    }
    // post-decrement
    if (afterIdent + 1 < s.length() && s.charAt(afterIdent) == '-' && s.charAt(afterIdent + 1) == '-') {
      if (!letNames.contains(name))
        throw new CompileException("Invalid post-decrement of non-mut variable " + name);
      String letRef = "let_" + name;
      String tmp = "_v" + varCount;
      out.append("(").append(tmp).append(" = ").append(letRef).append(", ").append(letRef).append(" = ")
          .append(letRef).append(" - 1, ").append(tmp).append(")");
      return new AtomResult(afterIdent + 2, varCount + 1);
    }
    Optional<ExprUtils.OpResult> idResOpt = ExprUtils.handleIdentifierWithLetsResult(s, idx, Optional.ofNullable(letNames));
    if (idResOpt.isPresent()) {
      ExprUtils.OpResult idRes = idResOpt.get();
      out.append(idRes.out);
      return new AtomResult(idRes.idx, varCount);
    }
    return null;
  }

  private static Optional<HandleResult> tryHandleIf(String s, int idx, StringBuilder out, int varCount,
      java.util.Set<String> letNames, java.util.Map<String, String> types,
      java.util.Map<String, String> funcAliases) throws CompileException {
    Optional<IfUtils.IfParts> partsOpt = IfUtils.tryFindIfParts(s, idx);
    if (!partsOpt.isPresent())
      return Optional.empty();
    IfUtils.IfParts parts = partsOpt.get();
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
    return Optional.of(new HandleResult(parts.elseEnd, newVar));
  }

  private static Optional<HandleResult> tryHandleBlock(String s, int idx, StringBuilder out, int varCount,
      java.util.Set<String> letNames, java.util.Map<String, String> types,
      java.util.Map<String, String> funcAliases) throws CompileException {
    if (s.charAt(idx) != '{')
      return Optional.empty();
    int j = BlockUtils.findClosingBrace(s, idx);
    if (j == -1)
      throw new CompileException("Unterminated block starting at index " + idx + " in expression: '" + s + "'");
    String inner = s.substring(idx + 1, j - 1).trim();
    ParseResult innerPr = parseExprWithLets(inner, varCount, letNames, types, funcAliases);
    out.append("(").append(innerPr.expr).append(")");
    return Optional.of(new HandleResult(j, innerPr.varCount));
  }
}
