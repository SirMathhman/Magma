import java.util.Set;
import java.util.HashSet;

public class Compiler {
  private final String target;

  public Compiler(String targetLanguage) {
    this.target = targetLanguage == null ? "" : targetLanguage.toLowerCase();
  }

  // Helper: find the next occurrence of key that is not part of a larger
  // identifier.
  // Returns the index immediately after the token if found, or -1 otherwise.
  private int findStandaloneTokenEnd(String src, String key, int start) {
    if (src == null || src.isEmpty())
      return -1;
    int idx = start;
    while (true) {
      idx = src.indexOf(key, idx);
      if (idx == -1)
        return -1;
      // ensure previous char (if any) is not part of an identifier
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

  // Returns: 0 = none found, 1 = valid call found (readInt()),
  // 2 = bare identifier found (invalid), 3 = call with arguments (invalid).
  private int findReadIntUsage(String src) {
    String key = "readInt";
    int idx = 0;
    boolean foundCall = false;
    while (true) {
      int end = findStandaloneTokenEnd(src, key, idx);
      if (end == -1)
        break;
      int j = end;
      while (j < src.length() && Character.isWhitespace(src.charAt(j)))
        j++;
      if (j < src.length() && src.charAt(j) == '(') {
        // find matching ')'
        int p = j + 1;
        int depth = 1;
        while (p < src.length() && depth > 0) {
          char ch = src.charAt(p);
          if (ch == '(')
            depth++;
          else if (ch == ')')
            depth--;
          p++;
        }
        if (depth != 0) {
          // unbalanced parens -> treat as invalid
          return 3;
        }
        int contentStart = j + 1;
        int contentEnd = p - 1; // exclusive
        boolean hasNonWs = false;
        for (int k = contentStart; k < contentEnd; k++) {
          if (!Character.isWhitespace(src.charAt(k))) {
            hasNonWs = true;
            break;
          }
        }
        if (hasNonWs)
          return 3; // call with args -> invalid
        foundCall = true;
        idx = p; // continue searching after ')'
      } else {
        return 2; // bare identifier — invalid
      }
    }
    return foundCall ? 1 : 0;
  }

  public Result<java.util.Set<Unit>, CompileError> compile(java.util.Set<Unit> units) {
    Set<Unit> out = new HashSet<>();
    for (Unit u : units) {
      String src = u.input() == null ? "" : u.input();
      int usage = findReadIntUsage(src);
      boolean wantsReadInt = usage == 1;
      if (usage == 2 || usage == 3) {
        return new Err<>(new CompileError("Invalid use of readInt"));
      }

      if ("typescript".equals(target)) {
        StringBuilder js = new StringBuilder();
        if (wantsReadInt) {
          js.append("const fs = require('fs');\n");
          js.append("const inRaw = fs.readFileSync(0, 'utf8');\n");
          js.append("let tok = (inRaw.match(/\\S+/) || [''])[0];\n");
          js.append("console.log(tok || '');\n");
        } else {
          js.append("// empty program\n");
        }
        out.add(new Unit(u.location(), ".js", js.toString()));
      } else if ("c".equals(target)) {
        StringBuilder c = new StringBuilder();
        c.append("#include <stdio.h>\n");
        if (wantsReadInt) {
          c.append("int main() { int x; if (scanf(\"%d\", &x)==1) printf(\"%d\", x); return 0; }");
        } else {
          c.append("int main() { return 0; }");
        }
        out.add(new Unit(u.location(), ".c", c.toString()));
      } else {
        out.add(u);
      }
    }
    return new Ok<>(out);
  }
}
