import java.util.Set;
import java.util.HashSet;

public class Compiler {
  private final String targetLanguage;

  public Compiler(String targetLanguage) {
    this.targetLanguage = targetLanguage;
  }

  public String getTargetLanguage() {
    return targetLanguage;
  }

  public Set<Unit> compile(Set<Unit> units) {
    if (targetLanguage == null) return units;
    String t = targetLanguage.toLowerCase();
    if (t.contains("c") && !t.contains("typescript")) {
      // emit simple .c stubs (no main) to trigger linker errors when building an executable
      Set<Unit> out = new HashSet<>();
      int idx = 0;
      for (Unit u : units) {
        String content = "/* generated C stub */\nint generated_function_" + idx + "() { return 0; }\n";
        out.add(new Unit(u.location(), ".c", content));
        idx++;
      }
      return out;
    }

    // For other target languages (typescript etc.) return units unchanged
    return units;
  }
}
