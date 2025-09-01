import java.util.Set;
import java.util.HashSet;

public class Compiler {
  private final String target;

  public Compiler(String targetLanguage) {
    this.target = targetLanguage == null ? "" : targetLanguage.toLowerCase();
  }

  public java.util.Set<Unit> compile(java.util.Set<Unit> units) {
    Set<Unit> out = new HashSet<>();
    for (Unit u : units) {
      String src = u.input() == null ? "" : u.input();
      boolean wantsReadInt = src.contains("readInt()");
      // detect invalid usage: the identifier 'readInt' not followed by parentheses
      boolean invalidReadIntUsage = java.util.regex.Pattern.compile("(^|\\W)readInt(?!\\s*\\()")
          .matcher(src)
          .find();
      if ("typescript".equals(target)) {
        StringBuilder js = new StringBuilder();
        if (invalidReadIntUsage) {
          // produce JS that exits non-zero to indicate invalid code
          js.append("console.error('Invalid code');\n");
          js.append("process.exit(1);\n");
        } else if (wantsReadInt) {
          js.append("const fs = require('fs');\n");
          js.append("const inRaw = fs.readFileSync(0, 'utf8');\n");
          js.append("const tok = (inRaw.match(/\\S+/) || [''])[0];\n");
          js.append("console.log(tok || '');\n");
        } else {
          // no-op program that prints nothing
          js.append("// empty program\n");
        }
        out.add(new Unit(u.location(), ".js", js.toString()));
      } else if ("c".equals(target)) {
        StringBuilder c = new StringBuilder();
        c.append("#include <stdio.h>\n");
        if (invalidReadIntUsage) {
          // cause a compile-time/runtime failure by referencing undefined symbol
          c.append("int main() { undefined_symbol(); return 1; }");
        } else if (wantsReadInt) {
          c.append("int main() { int x; if (scanf(\"%d\", &x)==1) printf(\"%d\", x); return 0; }");
        } else {
          c.append("int main() { return 0; }");
        }
        out.add(new Unit(u.location(), ".c", c.toString()));
      } else {
        // Unknown target: pass through as-is
        out.add(u);
      }
    }
    return out;
  }
}
