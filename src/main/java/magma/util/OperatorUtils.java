package magma.util;

public class OperatorUtils {
  // Try to handle logical/equality/relational operators starting at s[idx].
  // If handled, append the operator to out and return the new index. Otherwise
  // return -1.
  public static int tryHandleLogical(String s, int idx, StringBuilder out) {
    int len = s.length();
    int two = tryTwoCharOperators(s, idx, out, len);
    if (two != -1) {
      return two;
    }
    return tryOneCharOperators(s, idx, out);
  }

  private static int tryTwoCharOperators(String s, int idx, StringBuilder out, int len) {
    if (idx + 1 >= len)
      return -1;
    char a = s.charAt(idx);
    char b = s.charAt(idx + 1);
    if (a == '&' && b == '&') {
      out.append("&&");
      return idx + 2;
    }
    if (a == '|' && b == '|') {
      out.append("||");
      return idx + 2;
    }
    if (a == '=' && b == '=') {
      out.append("==");
      return idx + 2;
    }
    if (a == '!' && b == '=') {
      out.append("!=");
      return idx + 2;
    }
    if (a == '<' && b == '=') {
      out.append("<=");
      return idx + 2;
    }
    if (a == '>' && b == '=') {
      out.append(">=");
      return idx + 2;
    }
    return -1;
  }

  private static int tryOneCharOperators(String s, int idx, StringBuilder out) {
    char c = s.charAt(idx);
    if (c == '<' || c == '>') {
      out.append(c);
      return idx + 1;
    }
    return -1;
  }
}
