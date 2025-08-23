package magma.parse;

import magma.core.CompileException;
import magma.util.BlockUtils;
import java.util.Optional;

public final class StatementUtils {
  private StatementUtils() {
  }

  private static final class BlockHandleResult {
    final Optional<ExpressionParser.ParseResult> pr;
    final Optional<String> newCur;
    final int varCount;

    BlockHandleResult(Optional<ExpressionParser.ParseResult> pr, Optional<String> newCur, int varCount) {
      this.pr = pr;
      this.newCur = newCur;
      this.varCount = varCount;
    }
  }

  private static Optional<BlockHandleResult> tryHandleLeadingBlock(String cur, java.util.List<String> names,
      java.util.List<String> initStmtsAfter, java.util.Set<String> letNames, int varCount, String input,
      java.util.Map<String, String> types, java.util.Map<String, String> funcAliases) throws CompileException {
    if (!cur.startsWith("{"))
      return Optional.empty();
    int j = BlockUtils.findClosingBrace(cur, 0);
    if (j == -1)
      throw new CompileException("Unterminated block in statements in source: '" + input + "'");
    if (j >= cur.length()) {
      String innerAll = cur.substring(1, j - 1).trim();
      java.util.List<String> stmts = BlockUtils.splitTopLevelStatements(innerAll);
      for (String stmt : stmts) {
        varCount = handleStatement(stmt, names, initStmtsAfter, letNames, varCount, input, types, funcAliases);
      }
      String remainingFinal = innerAll;
      int lastSemi = -1;
      for (int i = 0; i < innerAll.length(); i++)
        if (innerAll.charAt(i) == ';')
          lastSemi = i;
      if (lastSemi != -1)
        remainingFinal = innerAll.substring(lastSemi + 1).trim();
      if (remainingFinal.isEmpty())
        throw new CompileException(
            "Invalid block: expected final expression inside block in source: '" + input + "'");
      ExpressionParser.ParseResult r = ExpressionParser.parseExprWithLets(remainingFinal, varCount, letNames,
          types, funcAliases);
      return Optional.of(new BlockHandleResult(Optional.of(r), Optional.empty(), varCount));
    }
    String inner = cur.substring(1, j - 1).trim();
    for (String stmt : BlockUtils.splitTopLevelStatements(inner)) {
      varCount = handleStatement(stmt, names, initStmtsAfter, letNames, varCount, input, types, funcAliases);
    }
    String newCur = cur.substring(j).trim();
    return Optional.of(new BlockHandleResult(Optional.empty(), Optional.of(newCur), varCount));
  }

  private static int tryConsumeTopLevelWhile(String cur, java.util.List<String> names,
      java.util.List<String> initStmtsAfter, java.util.Set<String> letNames, int varCount, String input,
      java.util.Map<String, String> types, java.util.Map<String, String> funcAliases) throws CompileException {
    if (!cur.startsWith("while "))
      return -1;
    int afterWhile = 6; // length of "while "
    int open = cur.indexOf('(', afterWhile);
    if (open == -1)
      throw new CompileException("Invalid while statement: missing '(' in source: '" + input + "'");
    int depth = 0;
    int close = -1;
    for (int i = open; i < cur.length(); i++) {
      char ch = cur.charAt(i);
      if (ch == '(')
        depth++;
      else if (ch == ')') {
        depth--;
        if (depth == 0) {
          close = i;
          break;
        }
      }
    }
    if (close == -1)
      throw new CompileException("Invalid while statement: unterminated condition in source: '" + input + "'");
    int thenOpen = cur.indexOf('{', close + 1);
    if (thenOpen == -1)
      throw new CompileException("Invalid while statement: missing '{' after condition in source: '" + input + "'");
    int thenEnd = BlockUtils.findClosingBrace(cur, thenOpen);
    if (thenEnd == -1)
      throw new CompileException("Invalid while statement: unterminated body in source: '" + input + "'");

    String stmt = cur.substring(0, thenEnd + 1).trim();
    int newVar = handleStatement(stmt, names, initStmtsAfter, letNames, varCount, input, types, funcAliases);
    // mutate the caller's cur by replacing it in place isn't possible; caller
    // should
    // continue the loop which will re-evaluate the (mutated) cur string in its own
    // context. To signal success, return the new varCount. The caller will then
    // update its cur by substringing off the consumed part.
    return newVar;
  }

  public static ExpressionParser.ParseResult processStatementsAndFinal(String cur, java.util.List<String> names,
      java.util.List<String> initStmtsAfter, java.util.Map<String, String> types,
      java.util.Map<String, String> funcAliases, int varCount, String input) throws CompileException {
    java.util.Set<String> letNames = new java.util.HashSet<>(names);
    while (true) {
      Optional<BlockHandleResult> bhrOpt = tryHandleLeadingBlock(cur, names, initStmtsAfter, letNames, varCount, input, types,
          funcAliases);
      if (bhrOpt.isPresent()) {
        BlockHandleResult bhr = bhrOpt.get();
        if (bhr.pr.isPresent())
          return bhr.pr.get();
        cur = bhr.newCur.orElse("");
        varCount = bhr.varCount;
        continue;
      }

      int consumed = tryConsumeTopLevelWhile(cur, names, initStmtsAfter, letNames, varCount, input, types,
          funcAliases);
      if (consumed != -1) {
        varCount = consumed;
        // advance cur past the consumed while statement
        int afterWhile = 6; // length of "while "
        int open = cur.indexOf('(', afterWhile);
        int depth = 0;
        int close = -1;
        for (int i = open; i < cur.length(); i++) {
          char ch = cur.charAt(i);
          if (ch == '(')
            depth++;
          else if (ch == ')') {
            depth--;
            if (depth == 0) {
              close = i;
              break;
            }
          }
        }
        int thenOpen = cur.indexOf('{', close + 1);
        int thenEnd = BlockUtils.findClosingBrace(cur, thenOpen);
        cur = cur.substring(thenEnd + 1).trim();
        continue;
      }

      int semi = BlockUtils.findTopLevelSemicolon(cur);
      if (semi == -1)
        break;
      String stmt = cur.substring(0, semi).trim();
      if (!stmt.isEmpty()) {
        varCount = handleStatement(stmt, names, initStmtsAfter, letNames, varCount, input, types, funcAliases);
      }
      cur = cur.substring(semi + 1).trim();
    }

    if (cur.isEmpty())
      throw new CompileException(
          "Invalid input: expected final expression after let bindings in source: '" + input + "'");

    ExpressionParser.ParseResult r = ExpressionParser.parseExprWithLets(cur, varCount, letNames, types, funcAliases);
    return r;
  }

  // Handle a statement (either nested let or assignment) and return updated
  // varCount.
  private static int handleStatement(String stmt, java.util.List<String> names, java.util.List<String> initStmtsAfter,
      java.util.Set<String> letNames, int varCount, String input, java.util.Map<String, String> types,
      java.util.Map<String, String> funcAliases)
      throws CompileException {
    if (stmt.startsWith("while ")) {
      return handleWhileStatement(stmt, names, initStmtsAfter, letNames, varCount, input, types, funcAliases);
    }
    if (stmt.startsWith("let ")) {
      return handleLetStatement(stmt, names, initStmtsAfter, letNames, varCount, input, types, funcAliases);
    }
    if (tryHandlePostIncDecStatement(stmt, initStmtsAfter, letNames, input))
      return varCount;
    int eq = stmt.indexOf('=');
    if (eq == -1) {
      throw new CompileException(
          "Invalid statement before final expression: '" + stmt + "' in source: '" + input + "'");
    }
    String left = stmt.substring(0, eq).trim();
    String right = stmt.substring(eq + 1).trim();
    if (left.contains(".")) {
      throw new CompileException("Cannot assign to struct field '" + left + "' in source: '" + input + "'");
    }
    boolean isDeref = false;
    String target = left;
    if (left.startsWith("*")) {
      isDeref = true;
      target = left.substring(1).trim();
      if (target.contains(".")) {
        throw new CompileException(
            "Cannot assign to struct field via dereference '" + left + "' in source: '" + input + "'");
      }
    }
    if (!letNames.contains(target)) {
      throw new CompileException("Invalid assignment to unknown name '" + target + "' in source: '" + input + "'");
    }
    ExpressionParser.ParseResult pr = ExpressionParser.parseExprWithLets(right, varCount, letNames, types, funcAliases);
    varCount = pr.varCount;
    if (isDeref) {
      initStmtsAfter.add("    *let_" + target + " = " + pr.expr + ";");
    } else {
      initStmtsAfter.add("    let_" + target + " = " + pr.expr + ";");
    }
    return varCount;
  }

  private static int handleWhileStatement(String stmt, java.util.List<String> names,
      java.util.List<String> initStmtsAfter, java.util.Set<String> letNames, int varCount, String input,
      java.util.Map<String, String> types, java.util.Map<String, String> funcAliases) throws CompileException {
    int afterWhile = 6; // length of "while "
    int open = stmt.indexOf('(', afterWhile);
    if (open == -1)
      throw new CompileException("Invalid while statement: missing '(' in source: '" + input + "'");
    int depth = 0;
    int close = -1;
    for (int i = open; i < stmt.length(); i++) {
      char ch = stmt.charAt(i);
      if (ch == '(')
        depth++;
      else if (ch == ')') {
        depth--;
        if (depth == 0) {
          close = i;
          break;
        }
      }
    }
    if (close == -1)
      throw new CompileException("Invalid while statement: unterminated condition in source: '" + input + "'");
    int thenOpen = stmt.indexOf('{', close + 1);
    if (thenOpen == -1)
      throw new CompileException("Invalid while statement: missing '{' after condition in source: '" + input + "'");
    int thenEnd = BlockUtils.findClosingBrace(stmt, thenOpen);
    if (thenEnd == -1)
      throw new CompileException("Invalid while statement: unterminated body in source: '" + input + "'");

    String cond = stmt.substring(open + 1, close).trim();
    ExpressionParser.ParseResult condPr = ExpressionParser.parseExprWithLets(cond, varCount, letNames, types,
        funcAliases);
    varCount = condPr.varCount;

    String inner = stmt.substring(thenOpen + 1, thenEnd - 1).trim();
    java.util.List<String> bodyStmts = BlockUtils.splitTopLevelStatements(inner);
    // Use temporary collections so lets declared inside body don't leak out, but
    // propagate any new varCount back to caller
    java.util.List<String> tempNames = new java.util.ArrayList<>(names);
    java.util.List<String> tempInit = new java.util.ArrayList<>();
    java.util.Set<String> tempLetNames = new java.util.HashSet<>(letNames);
    int localVarCount = varCount;
    for (String s : bodyStmts) {
      localVarCount = handleStatement(s, tempNames, tempInit, tempLetNames, localVarCount, input, types,
          funcAliases);
    }
    varCount = localVarCount;

    StringBuilder wb = new StringBuilder();
    wb.append("while (").append(condPr.expr).append(") {");
    for (String l : tempInit)
      wb.append(l);
    wb.append("}");
    initStmtsAfter.add(wb.toString());
    return varCount;
  }

  private static int handleLetStatement(String stmt, java.util.List<String> names,
      java.util.List<String> initStmtsAfter, java.util.Set<String> letNames, int varCount, String input,
      java.util.Map<String, String> types, java.util.Map<String, String> funcAliases) throws CompileException {
    int i2 = 4;
    if (stmt.startsWith("mut ", i2)) {
      i2 += 4;
    }
    StringBuilder ident2 = new StringBuilder();
    while (i2 < stmt.length()) {
      char cc = stmt.charAt(i2);
      if (Character.isWhitespace(cc) || cc == ':')
        break;
      ident2.append(cc);
      i2++;
    }
    if (ident2.length() == 0)
      throw new CompileException(
          "Invalid let declaration inside statements: '" + stmt + "' in source: '" + input + "'");
    String name2 = ident2.toString();
    int eq2 = stmt.indexOf('=', i2);
    if (eq2 == -1)
      throw new CompileException(
          "Invalid let declaration: missing '=' for binding '" + name2 + "' in source: '" + input + "'");
    String rhs = stmt.substring(eq2 + 1).trim();
    ExpressionParser.ParseResult pr2 = ExpressionParser.parseExprWithLets(rhs, varCount, letNames, types,
        funcAliases);
    names.add(name2);
    initStmtsAfter.add("    let_" + name2 + " = " + pr2.expr + ";");
    varCount = pr2.varCount;
    letNames.add(name2);
    return varCount;
  }

  private static boolean tryHandlePostIncDecStatement(String stmt, java.util.List<String> initStmtsAfter,
      java.util.Set<String> letNames, String input) throws CompileException {
    if (!(stmt.endsWith("++") || stmt.endsWith("--")))
      return false;
    String op = stmt.endsWith("++") ? "++" : "--";
    String left = stmt.substring(0, stmt.length() - 2).trim();
    boolean isDeref = false;
    String target = left;
    if (left.startsWith("*")) {
      isDeref = true;
      target = left.substring(1).trim();
    }
    if (target.contains(".")) {
      throw new CompileException("Cannot assign to struct field '" + target + "' in source: '" + input + "'");
    }
    if (!letNames.contains(target)) {
      throw new CompileException("Invalid assignment to unknown name '" + target + "' in source: '" + input + "'");
    }
    if (isDeref) {
      if (op.equals("++"))
        initStmtsAfter.add("    *let_" + target + " = *let_" + target + " + 1;");
      else
        initStmtsAfter.add("    *let_" + target + " = *let_" + target + " - 1;");
    } else {
      if (op.equals("++"))
        initStmtsAfter.add("    let_" + target + " = let_" + target + " + 1;");
      else
        initStmtsAfter.add("    let_" + target + " = let_" + target + " - 1;");
    }
    return true;
  }
}
