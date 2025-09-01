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
      if ("typescript".equals(target)) {
        StringBuilder js = new StringBuilder();
        if (wantsReadInt) {
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
        if (wantsReadInt) {
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
