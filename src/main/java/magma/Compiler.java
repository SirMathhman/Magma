package magma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Compiler {
  /**
   * Compiles the given source code string and returns the compiled output or a
   * CompileError wrapped in Result.
   */
  public static Result<String, CompileError> compile(String source) {
    // Minimal, non-regex parser/codegen tailored to the small test-suite.
    // Regexes were removed because parsing logic should be explicit and
    // generalizable; regexes tend to conflate tokenization and grammar.

    // Split statements on ';' and ignore the test prelude (intrinsic).
    String[] raw = source.split(";");
    List<String> stmts = new ArrayList<>();
    for (String part : raw) {
      String t = part.trim();
      if (!t.isEmpty() && !t.startsWith("intrinsic ")) {
        stmts.add(t);
      }
    }

    // Simple symbol table for lets: kind -> "i32" or "bool"
    Map<String, String> kinds = new HashMap<>();
    Map<String, String> boolValues = new HashMap<>();

    String finalExpr = "";

    // We'll build C declarations and code in-order so scanf calls map to stdin
    StringBuilder decls = new StringBuilder();
    StringBuilder code = new StringBuilder();

    final int[] tempCounter = new int[] { 0 };

    // Process statements: collect lets and remember final expression
    for (String s : stmts) {
      if (s.startsWith("let ")) {
        String rem = s.substring(4).trim();
        int eq = rem.indexOf('=');
        if (eq == -1) {
          return Result.err(new CompileError("malformed let", source));
        }
        String left = rem.substring(0, eq).trim();
        String init = rem.substring(eq + 1).trim();

        // Extract name and optional type
        String name;
        String type = "";
        int colon = left.indexOf(':');
        if (colon != -1) {
          name = left.substring(0, colon).trim();
          type = left.substring(colon + 1).trim();
        } else {
          name = left.split("\\s+")[0];
        }

        if (kinds.containsKey(name)) {
          return Result.err(new CompileError("duplicate variable: " + name, source));
        }

        if ("I32".equals(type) && ("true".equals(init) || "false".equals(init))) {
          return Result.err(new CompileError("type mismatch in let", source));
        }

        if ("readInt()".equals(init)) {
          kinds.put(name, "i32");
          decls.append(CompilerHelpers.declForInt(name));
          code.append(CompilerHelpers.codeForScanInt(name));
        } else if ("true".equals(init) || "false".equals(init)) {
          kinds.put(name, "bool");
          boolValues.put(name, init);
          decls.append(CompilerHelpers.declForAssignBool(name));
          code.append(CompilerHelpers.codeForAssignBool(name, init));
        } else {
          try {
            Integer.parseInt(init);
            kinds.put(name, "i32");
            decls.append(CompilerHelpers.declForAssignInt(name));
            code.append(CompilerHelpers.codeForAssign(name, init));
          } catch (NumberFormatException nfe) {
            String baseKind = kinds.getOrDefault(init, "i32");
            kinds.put(name, baseKind);
            if ("i32".equals(baseKind)) {
              decls.append(CompilerHelpers.declForAssignInt(name));
              code.append(CompilerHelpers.codeForAssign(name, init));
            } else {
              String bv = boolValues.getOrDefault(init, "false");
              kinds.put(name, "bool");
              boolValues.put(name, bv);
              decls.append(CompilerHelpers.declForAssignBool(name));
              code.append(CompilerHelpers.codeForAssignBool(name, bv));
            }
          }
        }
      } else {
        finalExpr = s;
      }
    }

    // Generate C program
    if (finalExpr.isEmpty()) {
      String cProgram = "#include <stdio.h>\n\nint main(void) {\n  return 0;\n}\n";
      return Result.ok(cProgram);
    }

    StringBuilder out = new StringBuilder();
    out.append(CodeGen.header());
    out.append(decls.toString());
    out.append(code.toString());

    // Binary ops
    int plus = finalExpr.indexOf('+');
    int minus = finalExpr.indexOf('-');
    if (plus != -1) {
      String left = finalExpr.substring(0, plus).trim();
      String right = finalExpr.substring(plus + 1).trim();
      // Type-check: both operands must be numeric (i32). Reject bools.
      boolean leftIsBool = "true".equals(left) || "false".equals(left) || "bool".equals(kinds.get(left));
      boolean rightIsBool = "true".equals(right) || "false".equals(right) || "bool".equals(kinds.get(right));
      if (leftIsBool || rightIsBool) {
        return Result.err(new CompileError("add requires numeric operands", source));
      }
      out.append(CompilerHelpers.emitBinaryPrint(left, right, "+", tempCounter, out));
    } else if (minus != -1) {
      String left = finalExpr.substring(0, minus).trim();
      String right = finalExpr.substring(minus + 1).trim();
      // Type-check for subtraction as well.
      boolean leftIsBool2 = "true".equals(left) || "false".equals(left) || "bool".equals(kinds.get(left));
      boolean rightIsBool2 = "true".equals(right) || "false".equals(right) || "bool".equals(kinds.get(right));
      if (leftIsBool2 || rightIsBool2) {
        return Result.err(new CompileError("sub requires numeric operands", source));
      }
      out.append(CompilerHelpers.emitBinaryPrint(left, right, "-", tempCounter, out));
    } else {
      // single operand
      String op = finalExpr.trim();
      if ("true".equals(op) || "false".equals(op)) {
        out.append("  printf(\"%s\", \"").append(op).append("\");\n");
      } else if (op.equals("readInt()")) {
        String tmp = CompilerHelpers.emitOperand(op, out, tempCounter);
        out.append(CodeGen.printfIntExpr(tmp));
      } else {
        // identifier or integer literal
        try {
          Integer.parseInt(op);
          out.append("  printf(\"%d\", ").append(op).append(");\n");
        } catch (NumberFormatException nfe) {
          if ("bool".equals(kinds.get(op))) {
            out.append(CodeGen.printfStrExpr(op));
          } else {
            out.append(CodeGen.printfIntExpr(op));
          }
        }
      }
    }

    out.append(CodeGen.footer());
    return Result.ok(out.toString());
  }

}
