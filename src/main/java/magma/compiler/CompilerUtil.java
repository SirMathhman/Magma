package magma.compiler;

import magma.parser.ParserUtils;

public final class CompilerUtil {
  private CompilerUtil() {
  }

  public static boolean isBracedNumeric(String s) {
    if (s == null)
      return false;
    String t = s.trim();
    if (t.length() < 3 || t.charAt(0) != '{' || t.charAt(t.length() - 1) != '}')
      return false;
    String inner = t.substring(1, t.length() - 1).trim();
    if (inner.isEmpty())
      return false;
    for (int i = 0; i < inner.length(); i++) {
      if (!Character.isDigit(inner.charAt(i)))
        return false;
    }
    return true;
  }

  public static boolean isPlainNumeric(String s) {
    if (s == null)
      return false;
    String t = s.trim();
    if (t.isEmpty())
      return false;
    for (int i = 0; i < t.length(); i++) {
      if (!Character.isDigit(t.charAt(i)))
        return false;
    }
    return true;
  }

  public static boolean isIdentifierChar(char ch) {
    return Character.isLetterOrDigit(ch) || ch == '_';
  }

  public static int findStandaloneTokenEnd(String src, String key, int start) {
    if (src == null || src.isEmpty())
      return -1;
    int idx = start;
    while (true) {
      idx = src.indexOf(key, idx);
      if (idx == -1)
        return -1;
      if (idx > 0) {
        char prev = src.charAt(idx - 1);
        if (Character.isLetterOrDigit(prev) || prev == '_') {
          idx += key.length();
          continue;
        }
      }
      return idx + key.length();
    }
  }

  public static int findStandaloneTokenIndex(String src, String key, int start) {
    int end = findStandaloneTokenEnd(src, key, start);
    if (end == -1)
      return -1;
    return end - key.length();
  }

  public static int skipWhitespace(String s, int idx) {
    int j = idx;
    while (j < s.length() && Character.isWhitespace(s.charAt(j)))
      j++;
    return j;
  }

  public static boolean isTopLevelPos(String s, int pos) {
    if (s == null || pos < 0)
      return false;
    int depth = 0;
    for (int i = 0; i < pos && i < s.length(); i++) {
      char ch = s.charAt(i);
      if (ch == '(')
        depth++;
      else if (ch == ')')
        depth--;
    }
    return depth == 0;
  }

  public static int findTopLevelOp(String s, String op) {
    if (s == null || op == null)
      return -1;
    int idx = 0;
    while (true) {
      idx = s.indexOf(op, idx);
      if (idx == -1)
        return -1;
      if (isTopLevelPos(s, idx))
        return idx;
      idx += 1;
    }
  }

  public static int countTopLevelArgs(String s) {
    if (s == null)
      return 0;
    String t = s.trim();
    if (t.isEmpty())
      return 0;
    // reuse ParserUtils
    return ParserUtils.splitTopLevel(t, ',', '(', ')').size();
  }

  public static int countParamsInType(String type) {
    if (type == null)
      return 0;
    String inner = getParamsInnerTypeSegment(type);
    if (inner == null || inner.isEmpty())
      return 0;
    return countTopLevelArgs(inner);
  }

  public static String getParamsInnerTypeSegment(String funcType) {
    if (funcType == null)
      return null;
    int arrow = funcType.indexOf("=>");
    if (arrow == -1)
      return null;
    String params = funcType.substring(0, arrow).trim();
    if (params.length() >= 2 && params.charAt(0) == '(' && params.charAt(params.length() - 1) == ')') {
      return params.substring(1, params.length() - 1).trim();
    }
    return null;
  }

  // Convert a param list like "(x : I32, y : I32)" into C params "(int x, int
  // y)".
  public static String paramsToC(String params) {
    if (params == null)
      return "()";
    String p = params.trim();
    if (p.length() >= 2 && p.charAt(0) == '(' && p.charAt(p.length() - 1) == ')') {
      String inner = p.substring(1, p.length() - 1).trim();
      if (inner.isEmpty())
        return "()";
      String[] parts = inner.split(",");
      StringBuilder out = new StringBuilder();
      out.append('(');
      boolean first = true;
      for (String part : parts) {
        String t = part.trim();
        if (t.isEmpty())
          continue;
        int colon = t.indexOf(':');
        String name = colon == -1 ? t : t.substring(0, colon).trim();
        String type = "int";
        if (colon != -1) {
          String typ = t.substring(colon + 1).trim();
          if (typ.equals("I32"))
            type = "int";
          else if (typ.equals("Bool"))
            type = "int";
          else
            type = "int";
        }
        if (!first)
          out.append(", ");
        out.append(type).append(' ').append(name);
        first = false;
      }
      out.append(')');
      return out.toString();
    }
    return "()";
  }

  // Remove type annotations from a parameter list like "(x : I32, y : I32)"
  // without using regular expressions.
  public static String stripParamTypes(String params) {
    if (params == null)
      return "";
    StringBuilder out = new StringBuilder();
    int i = 0;
    while (i < params.length()) {
      char c = params.charAt(i);
      if (c == ':') {
        do
          i++;
        while (i < params.length() && Character.isWhitespace(params.charAt(i)));
        while (i < params.length()) {
          char cc = params.charAt(i);
          if (cc == ',' || cc == ')')
            break;
          i++;
        }
      } else {
        out.append(c);
        i++;
      }
    }
    String temp = out.toString();
    StringBuilder norm = new StringBuilder();
    boolean lastWs = false;
    for (int j = 0; j < temp.length(); j++) {
      char ch = temp.charAt(j);
      if (Character.isWhitespace(ch)) {
        if (!lastWs) {
          norm.append(' ');
          lastWs = true;
        }
      } else {
        norm.append(ch);
        lastWs = false;
      }
    }
    String cleaned = norm.toString();
    cleaned = cleaned.replace(" ,", ",");
    cleaned = cleaned.replace("( ", "(");
    cleaned = cleaned.replace(" )", ")");
    return cleaned.trim();
  }

  // Scan left from index j (inclusive) for an identifier and return it, or
  // null if none found. Skips whitespace before the identifier.
  public static String identifierLeftOf(String s, int j) {
    if (s == null || j < 0)
      return null;
    int k = j;
    while (k >= 0 && Character.isWhitespace(s.charAt(k)))
      k--;
    if (k < 0)
      return null;
    int end = k + 1;
    while (k >= 0) {
      char c = s.charAt(k);
      if (Character.isLetterOrDigit(c) || c == '_')
        k--;
      else
        break;
    }
    int start = k + 1;
    if (start >= end)
      return null;
    return s.substring(start, end);
  }

  // Return the LHS identifier of a simple assignment statement `name = ...`,
  // or null if the statement is not an assignment.
  public static String getAssignmentLhs(String stmt) {
    if (stmt == null)
      return null;
    int idx = 0;
    while (true) {
      idx = stmt.indexOf('=', idx);
      if (idx == -1)
        break;
      if (idx + 1 < stmt.length() && stmt.charAt(idx + 1) == '=') {
        idx += 2;
        continue;
      }
      if (isTopLevelPos(stmt, idx)) {
        int leftIdx = idx - 1;
        if (leftIdx >= 0) {
          char pc = stmt.charAt(leftIdx);
          if (pc == '+' || pc == '-' || pc == '*' || pc == '/')
            leftIdx--;
        }
        return identifierLeftOf(stmt, leftIdx);
      }
      idx += 1;
    }

    // compound assignments like '+=', '-=', '*=', '/='
    String[] comp = new String[] { "+=", "-=", "*=", "/=" };
    for (String op : comp) {
      int i = findTopLevelOp(stmt, op);
      if (i != -1) {
        return identifierLeftOf(stmt, i - 1);
      }
    }

    // postfix 'name++' / 'name--'
    String[] incs = new String[] { "++", "--" };
    for (String op : incs) {
      int i = 0;
      while (true) {
        i = stmt.indexOf(op, i);
        if (i == -1)
          break;
        if (isTopLevelPos(stmt, i)) {
          String left = identifierLeftOf(stmt, i - 1);
          if (left != null) {
            return left;
          }
          int k = i + op.length();
          while (k < stmt.length() && Character.isWhitespace(stmt.charAt(k)))
            k++;
          if (k < stmt.length() && isIdentifierChar(stmt.charAt(k))) {
            int l = k;
            while (l < stmt.length() && isIdentifierChar(stmt.charAt(l)))
              l++;
            return stmt.substring(k, l);
          }
        }
        i += 1;
      }
    }
    return null;
  }

  public static boolean isAssignmentTo(String stmt, String varName) {
    String lhs = getAssignmentLhs(stmt);
    return lhs != null && lhs.equals(varName);
  }

  public static boolean isCompoundOrIncrement(String stmt) {
    if (stmt == null)
      return false;
    String[] ops = new String[] { "++", "--", "+=", "-=", "*=", "/=" };
    for (String op : ops) {
      if (findTopLevelOp(stmt, op) != -1)
        return true;
    }
    return false;
  }
}
