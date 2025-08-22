package magma;

public class LiteralUtils {
  
  // Try to append a literal (boolean or integer) at s[idx] into out. Returns
  // number of characters consumed (0 if none).
  public static int tryAppendLiteral(String s, int idx, StringBuilder out) {
    String tTrue = "true";
    if (idx + tTrue.length() <= s.length() && s.startsWith(tTrue, idx)) {
      out.append("1");
      return tTrue.length();
    }
    String tFalse = "false";
    if (idx + tFalse.length() <= s.length() && s.startsWith(tFalse, idx)) {
      out.append("0");
      return tFalse.length();
    }
    int len = s.length();
    if (idx >= len)
      return 0;
    char c = s.charAt(idx);
    if (!Character.isDigit(c))
      return 0;
    int start = idx;
    while (idx < len && Character.isDigit(s.charAt(idx)))
      idx++;
    out.append(s.substring(start, idx));
    return idx - start;
  }
}
