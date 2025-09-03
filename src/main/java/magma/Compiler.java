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
    // track mutability for variables declared with `let`.
    Map<String, Boolean> mutables = new HashMap<>();
    Map<String, String> boolValues = new HashMap<>();

    String finalExpr = "";

    // We'll build C declarations and code in-order so scanf calls map to stdin
    StringBuilder decls = new StringBuilder();
    StringBuilder code = new StringBuilder();

    final int[] tempCounter = new int[] { 0 };

    // Process statements: collect lets and remember final expression
    for (String s : stmts) {
      if (s.startsWith("let ")) {
        // support optional mut: `let mut x = ...`
        String rem = s.substring(4).trim();
        boolean isMutable = false;
        if (rem.startsWith("mut ")) {
          isMutable = true;
          rem = rem.substring(4).trim();
        }
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

        // record mutability (isMutable comes from optional `mut` parsed above)
        mutables.put(name, isMutable);

        if ("I32".equals(type) && ("true".equals(init) || "false".equals(init))) {
          return Result.err(new CompileError("type mismatch in let", source));
        }

        // Note: isMutable is currently informational; we allow later assignment
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
      } else if (s.contains("=") && !s.contains("==")) {
        // support standalone assignments like `x = readInt();`
        int eq = s.indexOf('=');
        String left = s.substring(0, eq).trim();
        String right = s.substring(eq + 1).trim();
        if (!kinds.containsKey(left)) {
          return Result.err(new CompileError("assignment to undeclared variable: " + left, source));
        }
        String targetKind = kinds.get(left);
        Boolean isMutableTarget = mutables.getOrDefault(left, false);
        if (!isMutableTarget) {
          return Result.err(new CompileError("assignment to immutable variable: " + left, source));
        }
        if ("readInt()".equals(right)) {
          Result<String, CompileError> check = checkAndAppendI32(targetKind, "scan", left, "", source, code);
          if (check instanceof Result.Err)
            return check;
        } else if ("true".equals(right) || "false".equals(right)) {
          if (!"bool".equals(targetKind)) {
            return Result.err(new CompileError("type mismatch in assignment", source));
          }
          code.append(CompilerHelpers.codeForAssignBool(left, right));
        } else {
          // assignment from literal or other identifier
          try {
            Integer.parseInt(right);
            Result<String, CompileError> assignCheck = checkAndAppendI32(targetKind, "assign", left, right, source,
                code);
            if (assignCheck instanceof Result.Err)
              return assignCheck;
          } catch (NumberFormatException nfe) {
            if (!kinds.containsKey(right)) {
              return Result.err(new CompileError("unknown RHS in assignment: " + right, source));
            }
            // allow assigning from another variable
            code.append(CompilerHelpers.codeForAssign(left, right));
          }
        }
      } else if (s.endsWith("++")) {
        String var = s.substring(0, s.length() - 2).trim();
        if (!kinds.containsKey(var)) {
          return Result.err(new CompileError("increment of undeclared variable: " + var, source));
        }
        if (!mutables.getOrDefault(var, false)) {
          return Result.err(new CompileError("increment of immutable variable: " + var, source));
        }
        Result<String, CompileError> ok = ensureI32(kinds.get(var), source);
        if (ok instanceof Result.Err)
          return ok;
        code.append(CompilerHelpers.codeForAssign(var, var + " + 1"));
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
    // support simple compile-time if-expression when condition is a literal
    if (finalExpr.startsWith("if")) {
      int open = finalExpr.indexOf('(');
      int close = (open >= 0) ? findMatchingParen(finalExpr, open) : -1;
      if (open != -1 && close != -1 && close > open) {
        String cond = finalExpr.substring(open + 1, close).trim();
        String rest = finalExpr.substring(close + 1).trim();
        int elseIdx = rest.indexOf(" else ");
        if (elseIdx != -1) {
          String thenExpr = rest.substring(0, elseIdx).trim();
          String elseExpr = rest.substring(elseIdx + 6).trim();
          if ("true".equals(cond) || "false".equals(cond)) {
            finalExpr = ("true".equals(cond)) ? thenExpr : elseExpr;
          } else if (cond.contains("==")) {
            // runtime if with equality condition
            int ee = cond.indexOf("==");
            String l = cond.substring(0, ee).trim();
            String r = cond.substring(ee + 2).trim();
            Result<String, CompileError> check = ensureNumericOperands(l, r, kinds, source, "if");
            if (check instanceof Result.Err) {
              return check;
            }
            String lv = CompilerHelpers.emitOperand(l, out, tempCounter);
            String rv = CompilerHelpers.emitOperand(r, out, tempCounter);
            String ctmp = "c" + (tempCounter[0]++);
            out.append(CodeGen.declareInt(ctmp));
            out.append(CodeGen.assign(ctmp, "(" + lv + ") == (" + rv + ")"));
            out.append("  if (" + ctmp + ") {\n");
            out.append(printSingleExpr(thenExpr, kinds, out, tempCounter));
            out.append("  } else {\n");
            out.append(printSingleExpr(elseExpr, kinds, out, tempCounter));
            out.append("  }\n");
            out.append(CodeGen.footer());
            return Result.ok(out.toString());
          } else {
            return Result.err(new CompileError("unsupported if condition (only literal true/false supported)", source));
          }
        }
      }
    }

    int plus = finalExpr.indexOf('+');
    int minus = finalExpr.indexOf('-');
    int eqeq = finalExpr.indexOf("==");
    if (eqeq != -1) {
      String left = finalExpr.substring(0, eqeq).trim();
      String right = finalExpr.substring(eqeq + 2).trim();
      Result<String, CompileError> eqCheck = ensureNumericOperands(left, right, kinds, source, "eq");
      if (eqCheck instanceof Result.Err)
        return eqCheck;
      out.append(CompilerHelpers.emitEqPrint(left, right, tempCounter, out));
    } else if (plus != -1) {
      String left = finalExpr.substring(0, plus).trim();
      String right = finalExpr.substring(plus + 1).trim();
      Result<String, CompileError> addCheck = ensureNumericOperands(left, right, kinds, source, "add");
      if (addCheck instanceof Result.Err)
        return addCheck;
      out.append(CompilerHelpers.emitBinaryPrint(left, right, "+", tempCounter, out));
    } else if (minus != -1) {
      String left = finalExpr.substring(0, minus).trim();
      String right = finalExpr.substring(minus + 1).trim();
      Result<String, CompileError> subCheck = ensureNumericOperands(left, right, kinds, source, "sub");
      if (subCheck instanceof Result.Err)
        return subCheck;
      out.append(CompilerHelpers.emitBinaryPrint(left, right, "-", tempCounter, out));
    } else {
      out.append(printSingleExpr(finalExpr.trim(), kinds, out, tempCounter));
    }

    out.append(CodeGen.footer());
    return Result.ok(out.toString());
  }

  private static Result<String, CompileError> checkAndAppendI32(String targetKind, String kind, String left,
      String right, String source, StringBuilder code) {
    Result<String, CompileError> ok = ensureI32(targetKind, source);
    if (ok instanceof Result.Err)
      return ok;
    if ("scan".equals(kind)) {
      code.append(CompilerHelpers.codeForScanInt(left));
    } else if ("assign".equals(kind)) {
      code.append(CompilerHelpers.codeForAssign(left, right));
    }
    return Result.ok("");
  }

  private static Result<String, CompileError> ensureI32(String targetKind, String source) {
    if (!"i32".equals(targetKind)) {
      return Result.err(new CompileError("type mismatch in assignment", source));
    }
    return Result.ok("");
  }

  private static boolean isBoolToken(String token, Map<String, String> kinds) {
    return "true".equals(token) || "false".equals(token) || "bool".equals(kinds.get(token));
  }

  private static Result<String, CompileError> ensureNumericOperands(String left, String right,
      Map<String, String> kinds,
      String source, String opName) {
    if (isBoolToken(left, kinds) || isBoolToken(right, kinds)) {
      return Result.err(new CompileError(opName + " requires numeric operands", source));
    }
    return Result.ok("");
  }

  private static int findMatchingParen(String s, int openIdx) {
    int depth = 0;
    for (int i = openIdx; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '(') {
        depth++;
      } else if (c == ')') {
        depth--;
        if (depth == 0) {
          return i;
        }
      }
    }
    return -1;
  }

  private static String printSingleExpr(String op, Map<String, String> kinds, StringBuilder out, int[] tempCounter) {
    String expr = op.trim();
    if ("true".equals(expr) || "false".equals(expr)) {
      return CodeGen.printfStrExpr("\"" + expr + "\"");
    }
    if (expr.equals("readInt()")) {
      String tmp = CompilerHelpers.emitOperand(expr, out, tempCounter);
      return CodeGen.printfIntExpr(tmp);
    }
    try {
      Integer.parseInt(expr);
      return CodeGen.printfIntExpr(expr);
    } catch (NumberFormatException nfe) {
      if ("bool".equals(kinds.get(expr))) {
        return CodeGen.printfStrExpr(expr);
      } else {
        return CodeGen.printfIntExpr(expr);
      }
    }
  }

}
