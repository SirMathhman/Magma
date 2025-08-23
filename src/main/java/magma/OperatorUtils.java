package magma;

public class OperatorUtils {
    // Try to handle logical operators '&&' and '||' at s[idx].
    // If handled, append the operator to out and return the new index.
    // Otherwise return -1.
    public static int tryHandleLogical(String s, int idx, StringBuilder out) {
        int len = s.length();
        if (idx + 1 < len && s.charAt(idx) == '&' && s.charAt(idx + 1) == '&') {
            out.append("&&");
            return idx + 2;
        }
        if (idx + 1 < len && s.charAt(idx) == '|' && s.charAt(idx + 1) == '|') {
            out.append("||");
            return idx + 2;
        }
        return -1;
    }
}
