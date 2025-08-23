package magma.parse;

import magma.core.CompileException;
import magma.util.BlockUtils;

public final class StatementUtils {
  private StatementUtils() {
  }

  public static ExpressionParser.ParseResult processStatementsAndFinal(String cur, java.util.List<String> names,
      java.util.List<String> initStmtsAfter, java.util.Map<String, String> types,
      java.util.Map<String, String> funcAliases, int varCount, String input) throws CompileException {
    java.util.Set<String> letNames = new java.util.HashSet<>(names);
    while (true) {
      if (cur.startsWith("{")) {
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
          return r;
        }
        String inner = cur.substring(1, j - 1).trim();
        for (String stmt : BlockUtils.splitTopLevelStatements(inner)) {
          varCount = handleStatement(stmt, names, initStmtsAfter, letNames, varCount, input, types, funcAliases);
        }
        cur = cur.substring(j).trim();
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
    if (stmt.startsWith("let ")) {
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
}
