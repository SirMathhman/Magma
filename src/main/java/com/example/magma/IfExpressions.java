package com.example.magma;

final class IfExpressions {
  private IfExpressions() {
  }

  static String transform(String s) {
    if (s == null || s.isEmpty())
      return s;
    StringBuilder out = new StringBuilder();
    int i = 0;
    while (i < s.length()) {
      int idx = indexOfIfWord(s, i);
      if (idx == -1) {
        out.append(s.substring(i));
        break;
      }
      out.append(s.substring(i, idx));
      ParseResult pr = parseIfExpression(s, idx);
      if (pr == null) {
        out.append(s.substring(idx));
        break;
      }
      out.append(pr.transformed);
      i = pr.nextIndex;
    }
    return out.toString();
  }

  private static int indexOfIfWord(String s, int start) {
    int idx = s.indexOf("if", start);
    while (idx != -1) {
      boolean leftOk = (idx == 0) || !Character.isLetterOrDigit(s.charAt(idx - 1));
      int after = idx + 2;
      boolean rightOk = after >= s.length() || !Character.isLetterOrDigit(s.charAt(after));
      if (leftOk && rightOk)
        return idx;
      idx = s.indexOf("if", idx + 2);
    }
    return -1;
  }

  private static final class ParseResult {
    final String transformed;
    final int nextIndex;

    ParseResult(String transformed, int nextIndex) {
      this.transformed = transformed;
      this.nextIndex = nextIndex;
    }
  }

  private static ParseResult parseIfExpression(String s, int start) {
    ParseParts parts = parseIfParts(s, start);
    if (parts == null)
      return null;
    String condT = transform(parts.cond);
    String thenT = transform(parts.thenExpr);
    String elseT = transform(parts.elseExpr);
    String transformed = "(" + condT + " ? " + thenT + " : " + elseT + ")";
    return new ParseResult(transformed, parts.endIndex);
  }

  private static record ParseParts(String cond, String thenExpr, String elseExpr, int endIndex) {
  }

  private static ParseParts parseIfParts(String s, int start) {
    if (s == null)
      return null;
    int i = start + 2; // skip 'if'
    i = Structs.skipWhitespace(s, i);
    int condStart = i;

    ConditionParseResult condRes = parseCondition(s, condStart);
    if (condRes == null)
      return null;
    ThenElseParseResult te = parseThenElse(s, condRes.afterThenIndex);
    if (te == null)
      return null;
    return new ParseParts(condRes.cond, te.thenExpr, te.elseExpr, te.endIndex);
  }

  private static class ConditionParseResult {
    final String cond;
    final int afterThenIndex;

    ConditionParseResult(String cond, int afterThenIndex) {
      this.cond = cond;
      this.afterThenIndex = afterThenIndex;
    }
  }

  private static ConditionParseResult parseCondition(String s, int condStart) {
    int thenIdx = Structs.findTopLevelToken(s, condStart, "then");
    if (thenIdx == -1)
      return null;
    String cond = s.substring(condStart, thenIdx).trim();
    int afterThen = Structs.skipWhitespace(s, thenIdx + 4);
    return new ConditionParseResult(cond, afterThen);
  }

  private static class ThenElseParseResult {
    final String thenExpr;
    final String elseExpr;
    final int endIndex;

    ThenElseParseResult(String thenExpr, String elseExpr, int endIndex) {
      this.thenExpr = thenExpr;
      this.elseExpr = elseExpr;
      this.endIndex = endIndex;
    }
  }

  private static ThenElseParseResult parseThenElse(String s, int afterThen) {
    int elseIdx = Structs.findTopLevelToken(s, afterThen, "else");
    if (elseIdx == -1)
      return null;
    String thenExpr = s.substring(afterThen, elseIdx).trim();
    int afterElse = Structs.skipWhitespace(s, elseIdx + 4);
    int endIdx = findElseExpressionEnd(s, afterElse);
    if (endIdx == -1)
      return null;
    String elseExpr = s.substring(afterElse, endIdx).trim();
    return new ThenElseParseResult(thenExpr, elseExpr, endIdx);
  }

  private static int findElseExpressionEnd(String s, int start) {
    if (s == null || start >= s.length())
      return -1;
    char c = s.charAt(start);
    if (c == '{')
      return findElseBlockEnd(s, start);
    if (c == '(')
      return findElseParenEnd(s, start);
    return findElseSimpleEnd(s, start);
  }

  private static int findElseBlockEnd(String s, int start) {
    int close = Structs.findMatchingBrace(s, start);
    if (close == -1)
      return -1;
    return close + 1;
  }

  private static int findElseParenEnd(String s, int start) {
    int close = Structs.findClosingIndex(s, start + 1);
    if (close == -1)
      return -1;
    return close + 1;
  }

  private static int findElseSimpleEnd(String s, int start) {
    int j = start;
    while (j < s.length()) {
      char ch = s.charAt(j);
      if (ch == ';' || ch == ')' || ch == '\n')
        break;
      j++;
    }
    return j;
  }

}
