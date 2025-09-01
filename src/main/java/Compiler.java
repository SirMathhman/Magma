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
    // Emit TypeScript bundle when requested
    if (t.contains("typescript")) {
      String src = CompilerUtil.combineUnitsInput(units);
      if (src.isBlank()) {
        return units;
      }

      java.util.Map<String, String> emitted = CompilerUtil.emitTsAndJs(src);
      Location locTs = CompilerUtil.locOrDefault(units);
      Set<Unit> outTs = new HashSet<>();
      outTs.add(new Unit(locTs, ".ts", emitted.get(".ts")));
      outTs.add(new Unit(locTs, ".js", emitted.get(".js")));
      return outTs;
    }
    if (t.contains("c") && !t.contains("typescript")) {
      // Simple C emitter:
      // - If all input units are empty, return units unchanged.
      // - If the combined source contains a call to readInt(), emit a C program
      // that implements readInt() (scanf from stdin) and prints the returned
      // integer to stdout. This keeps the compiler small and sufficient for
      // the tests which exercise reading an integer.
      String src = CompilerUtil.combineUnitsInput(units);
      if (src.isBlank()) {
        return units;
      }

      StringBuilder sb = new StringBuilder();
      sb.append("#include <stdio.h>\n");
      sb.append("#include <stdlib.h>\n\n");

      // Provide a simple readInt implementation that reads one integer from stdin.
      sb.append("int readInt() {\n");
      sb.append("  int x = 0;\n");
      sb.append("  if (scanf(\"%d\", &x) == 1) return x;\n");
      sb.append("  return 0;\n");
      sb.append("}\n\n");

      // If the source appears to call readInt() (excluding extern declarations),
      // evaluate the combined source as an expression so multiple calls
      // (e.g. readInt() + readInt()) are executed and the result printed.
      String stripped = CompilerUtil.stripExterns(src);
      if (stripped.contains("readInt()")) {
        String filteredBody = CompilerUtil.unwrapBracesIfSingleExpression(stripped);
        // Translate and clean struct/constructor patterns for C before other processing
        filteredBody = CompilerUtil.translateStructsForC(filteredBody);
        // Extract any #define lines that may appear anywhere in filteredBody
        // (translateStructsForC can emit defines). Remove them from the
        // expression so they aren't left inside 'int v = (...)'.
        String defines = "";
        if (filteredBody.contains("#define")) {
          String[] lines = filteredBody.split("\n", -1);
          StringBuilder rest = new StringBuilder();
          StringBuilder dacc = new StringBuilder();
          for (String L : lines) {
            if (L.trim().startsWith("#define")) {
              dacc.append(L.trim()).append('\n');
            } else {
              rest.append(L).append('\n');
            }
          }
          defines = dacc.toString();
          // remove final trailing newline from rest
          if (rest.length() > 0 && rest.charAt(rest.length() - 1) == '\n') rest.setLength(rest.length() - 1);
          filteredBody = rest.toString();
        }
        // Translate arrow-style function defs to C before further processing
        filteredBody = CompilerUtil.translateFnArrowToC(filteredBody);
        // translate 'let mut' -> 'int ' and 'let ' -> 'const int ' without regex
        filteredBody = CompilerUtil.protectLetMut(filteredBody);
        filteredBody = CompilerUtil.replaceLetWithConst(filteredBody);
        // translate JS-style 'const ' into C declarations 'const int '
        filteredBody = filteredBody.replace("const ", "const int ");
        // restore mutable placeholder to C mutable type
        filteredBody = filteredBody.replace("__LET_MUT__", "int ");
  // remember if original stripped source contained boolean literals
  // so we can decide to print "true"/"false" later. Use `stripped`
  // (pre-transform) so later translations don't obscure the tokens.
  boolean hadBoolLiteral = stripped.contains("true") || stripped.contains("false");
        if (filteredBody.isBlank()) {
          sb.append("int main() { return 0; }\n");
        } else {
          filteredBody = CompilerUtil.translateIfElseToTernary(filteredBody);
          filteredBody = CompilerUtil.translateBoolForC(filteredBody);
          // hoist readInt() inside for loop conditions to avoid consuming input
          // multiple times during loop evaluation. Use 'int' declaration for C.
          filteredBody = CompilerUtil.hoistReadIntInForWithPrefix(filteredBody, "int ");
          // extract any top-level function definitions produced by arrow->C
          String[] funcsAndRest = CompilerUtil.extractTopLevelIntFunctions(filteredBody);
          String topFuncs = funcsAndRest[0];
          filteredBody = funcsAndRest[1];
          String[] headTail = CompilerUtil.splitHeadTail(filteredBody);
          String head = headTail[0];
          String tail = headTail[1];

          // Build main body separately so we can emit top-level functions
          StringBuilder mainSb = new StringBuilder();
          mainSb.append("int main() {\n");
          if (!head.isBlank()) {
            // indent head lines
            for (String hline : head.split(";")) {
              String hl = hline.trim();
              if (!hl.isBlank()) {
                mainSb.append("  ").append(hl);
                if (!hl.endsWith(";"))
                  mainSb.append(";");
                mainSb.append("\n");
              }
            }
          }
          boolean isBool = false;
          if (!tail.isBlank()) {
            mainSb.append("  int v = (").append(tail).append(");\n");
            String ttest = tail;
            if (!ttest.contains("?")
                && (ttest.contains("==") || ttest.contains("!=") || ttest.contains("<=") || ttest.contains(">=")
                    || ttest.contains("<") || ttest.contains(">") || ttest.contains("&&") || ttest.contains("||"))) {
              isBool = true;
            }
            // if the original had boolean literals, treat as boolean result
            if (hadBoolLiteral) isBool = true;
          } else {
            mainSb.append("  int v = 0;\n");
          }
          if (isBool) {
            mainSb.append("  printf(\"%s\", v ? \"true\" : \"false\");\n");
          } else {
            mainSb.append("  printf(\"%d\", v);\n");
          }
          mainSb.append("  return 0;\n");
          mainSb.append("}\n");
          // emit any extracted defines and top-level functions after helper implementations
          if (!defines.isBlank()) {
            sb.append(defines).append("\n");
          }
          if (!topFuncs.isBlank()) {
            sb.append(topFuncs).append("\n");
          }
          // append the main body
          sb.append(mainSb.toString());
        }
      } else {
        // If there is no readInt(), run the same minimal translation pipeline
        // so top-level enums/structs/impls are removed before emitting C.
        String filteredBody = CompilerUtil.unwrapBracesIfSingleExpression(stripped).trim();
        if (filteredBody.isBlank()) {
          sb.append("int main() { return 0; }\n");
        } else {
          // Apply full C translation pipeline (same as readInt path):
          // - extract defines produced by translateStructsForC
          // - translate arrow functions, let/const, ternaries, bools
          // - hoist readInt in for loops, extract top-level functions
          filteredBody = CompilerUtil.translateStructsForC(filteredBody);
          String definesElse = "";
          if (filteredBody.contains("#define")) {
            String[] lines = filteredBody.split("\n", -1);
            StringBuilder rest = new StringBuilder();
            StringBuilder dacc = new StringBuilder();
            for (String L : lines) {
              if (L.trim().startsWith("#define")) {
                dacc.append(L.trim()).append('\n');
              } else {
                rest.append(L).append('\n');
              }
            }
            definesElse = dacc.toString();
            if (rest.length() > 0 && rest.charAt(rest.length() - 1) == '\n') rest.setLength(rest.length() - 1);
            filteredBody = rest.toString();
          }
          // remember if original stripped source contained boolean literals
          boolean hadBoolLiteralElse = stripped.contains("true") || stripped.contains("false");
          filteredBody = CompilerUtil.translateFnArrowToC(filteredBody);
          filteredBody = CompilerUtil.protectLetMut(filteredBody);
          filteredBody = CompilerUtil.replaceLetWithConst(filteredBody);
          filteredBody = filteredBody.replace("const ", "const int ");
          filteredBody = filteredBody.replace("__LET_MUT__", "int ");
          filteredBody = CompilerUtil.translateIfElseToTernary(filteredBody);
          filteredBody = CompilerUtil.translateBoolForC(filteredBody);
          filteredBody = CompilerUtil.hoistReadIntInForWithPrefix(filteredBody, "int ");
          String[] funcsAndRestElse = CompilerUtil.extractTopLevelIntFunctions(filteredBody);
          String topFuncsElse = funcsAndRestElse[0];
          filteredBody = funcsAndRestElse[1];
          String[] headTailElse = CompilerUtil.splitHeadTail(filteredBody);
          String headElse = headTailElse[0];
          String tailElse = headTailElse[1];

          StringBuilder mainSbElse = new StringBuilder();
          mainSbElse.append("int main() {\n");
          if (!headElse.isBlank()) {
            for (String hline : headElse.split(";")) {
              String hl = hline.trim();
              if (!hl.isBlank()) {
                mainSbElse.append("  ").append(hl);
                if (!hl.endsWith(";")) mainSbElse.append(";");
                mainSbElse.append("\n");
              }
            }
          }
          boolean isBoolElse = false;
          if (!tailElse.isBlank()) {
            mainSbElse.append("  int v = (").append(tailElse).append(");\n");
            String ttest = tailElse;
            if (!ttest.contains("?")
                && (ttest.contains("==") || ttest.contains("!=") || ttest.contains("<=") || ttest.contains(">=")
                    || ttest.contains("<") || ttest.contains(">") || ttest.contains("&&") || ttest.contains("||"))) {
              isBoolElse = true;
            }
            if (hadBoolLiteralElse) isBoolElse = true;
          } else {
            mainSbElse.append("  int v = 0;\n");
          }
          if (isBoolElse) {
            mainSbElse.append("  printf(\"%s\", v ? \"true\" : \"false\");\n");
          } else {
            mainSbElse.append("  printf(\"%d\", v);\n");
          }
          mainSbElse.append("  return 0;\n");
          mainSbElse.append("}\n");

          if (!definesElse.isBlank()) {
            sb.append(definesElse).append("\n");
          }
          if (!topFuncsElse.isBlank()) {
            sb.append(topFuncsElse).append("\n");
          }
          sb.append(mainSbElse.toString());
        }
      }

      // Use the location of the first unit if available, otherwise a default
      Location loc = CompilerUtil.locOrDefault(units);
      Set<Unit> out = new HashSet<>();
      out.add(new Unit(loc, ".c", sb.toString()));
      return out;
    }

    // For other target languages (typescript etc.) return units unchanged
    return units;
  }
}
