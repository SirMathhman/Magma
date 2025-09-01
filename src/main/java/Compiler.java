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
    if (targetLanguage == null)
      return units;
    String t = targetLanguage.toLowerCase();
    if (t.contains("c") && !t.contains("typescript")) {
      // Emit a single .c file containing all generated functions and a main()
      StringBuilder sb = new StringBuilder();
      sb.append("/* generated C bundle */\n");
      int idx = 0;
      for (Unit u : units) {
        sb.append("int generated_function_").append(idx).append("() { return 0; }\n");
        idx++;
      }
      // Provide a main that calls the first generated function (or returns 0)
      sb.append("int main() {\n");
      if (idx > 0) {
        sb.append("  return generated_function_0();\n");
      } else {
        sb.append("  return 0;\n");
      }
      sb.append("}\n");

      // Use the location of the first unit if available, otherwise a default
      Location loc = units.stream().findFirst().map(Unit::location)
          .orElse(new Location(java.util.Collections.emptyList(), "main"));
      Set<Unit> out = new HashSet<>();
      out.add(new Unit(loc, ".c", sb.toString()));
      return out;
    }

    // For other target languages (typescript etc.) return units unchanged
    return units;
  }
}
