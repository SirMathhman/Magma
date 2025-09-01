import java.util.Set;
import java.util.HashSet;

public class Compiler {
  private final String targetLanguage;

  public Compiler(String targetLanguage) {
    this.targetLanguage = targetLanguage;
  }

  // Helper to build the main function snippet given extracted pieces.
  private String buildMainSnippet(String defines, String topFuncs, String head, String tail, String preBoolTail) {
    StringBuilder out = new StringBuilder();
    // emit any extracted defines and top-level functions after helper implementations
    if (defines != null && !defines.isBlank()) {
      out.append(defines).append("\n");
    }
    if (topFuncs != null && !topFuncs.isBlank()) {
      out.append(topFuncs).append("\n");
    }

    StringBuilder mainSb = new StringBuilder();
    mainSb.append("int main() {\n");
    if (head != null && !head.isBlank()) {
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
    if (tail != null && !tail.isBlank()) {
      mainSb.append("  int v = (").append(tail).append(");\n");
      String ttest = preBoolTail == null ? "" : preBoolTail.trim();
      if (!ttest.isBlank()) {
        if (!ttest.contains("?") && (
              ttest.equals("true") || ttest.equals("false") ||
              ttest.contains("==") || ttest.contains("!=") || ttest.contains("<=") || ttest.contains(">=") ||
              ttest.contains("<") || ttest.contains(">") || ttest.contains("&&") || ttest.contains("||") ||
              ttest.contains("!")
            )) {
          isBool = true;
        }
      }
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

    out.append(mainSb.toString());
    return out.toString();
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
  String[] defRest = CompilerUtil.splitOutDefines(filteredBody);
  String defines = defRest[0];
  filteredBody = defRest[1];
        // Translate arrow-style function defs to C before further processing
        filteredBody = CompilerUtil.translateFnArrowToC(filteredBody);
        // translate 'let mut' -> 'int ' and 'let ' -> 'const int ' without regex
        filteredBody = CompilerUtil.protectLetMut(filteredBody);
        filteredBody = CompilerUtil.replaceLetWithConst(filteredBody);
        // translate JS-style 'const ' into C declarations 'const int '
        filteredBody = filteredBody.replace("const ", "const int ");
        // restore mutable placeholder to C mutable type
        filteredBody = filteredBody.replace("__LET_MUT__", "int ");
        if (filteredBody.isBlank()) {
          sb.append("int main() { return 0; }\n");
        } else {
          filteredBody = CompilerUtil.translateIfElseToTernary(filteredBody);
          // Preserve a pre-bool-translation copy to detect if the tail is actually
          // a boolean expression or literal (and not just a ternary with boolean
          // condition). This avoids printing "true" for numeric ternary results.
          String preBoolTransBody = filteredBody;
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
          // Also split the pre-bool-translated body to inspect the original tail
          // for boolean-ness (e.g., 'true', 'a == b', '!flag').
          String preBoolTail = CompilerUtil.splitHeadTail(preBoolTransBody)[1];

          // Build main, emit any defines/top-level functions, and append to sb.
          sb.append(buildMainSnippet(defines, topFuncs, head, tail, preBoolTail));
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
          String[] defRestElse = CompilerUtil.splitOutDefines(filteredBody);
          String definesElse = defRestElse[0];
          filteredBody = defRestElse[1];
          filteredBody = CompilerUtil.translateFnArrowToC(filteredBody);
          filteredBody = CompilerUtil.protectLetMut(filteredBody);
          filteredBody = CompilerUtil.replaceLetWithConst(filteredBody);
          filteredBody = filteredBody.replace("const ", "const int ");
          filteredBody = filteredBody.replace("__LET_MUT__", "int ");
          filteredBody = CompilerUtil.translateIfElseToTernary(filteredBody);
          String preBoolTransBodyElse = filteredBody;
          filteredBody = CompilerUtil.translateBoolForC(filteredBody);
          filteredBody = CompilerUtil.hoistReadIntInForWithPrefix(filteredBody, "int ");
          String[] funcsAndRestElse = CompilerUtil.extractTopLevelIntFunctions(filteredBody);
          String topFuncsElse = funcsAndRestElse[0];
          filteredBody = funcsAndRestElse[1];
          String[] headTailElse = CompilerUtil.splitHeadTail(filteredBody);
          String headElse = headTailElse[0];
          String tailElse = headTailElse[1];
          String preBoolTailElse = CompilerUtil.splitHeadTail(preBoolTransBodyElse)[1];

          sb.append(buildMainSnippet(definesElse, topFuncsElse, headElse, tailElse, preBoolTailElse));
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
