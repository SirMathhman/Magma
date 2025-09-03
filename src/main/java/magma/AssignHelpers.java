package magma;

import java.util.Map;

/**
 * Small helper class for assignment-related utilities to keep Compiler lean.
 */
final class AssignHelpers {
  private AssignHelpers() {
  }

  static Result<String, CompileError> handleAssignFromIdentifier(String left, String right,
      Map<String, String> kinds, StringBuilder code, String source) {
    if (!kinds.containsKey(right)) {
      return Result.err(new CompileError("unknown RHS in assignment: " + right, source));
    }
    code.append(CodeGen.assign(left, right));
    return Result.ok("");
  }

  static Result<String, CompileError> resolveRhsToI32Expr(String right, Map<String, String> kinds,
      StringBuilder decls, StringBuilder code, int[] tempCounter, String source) {
    if ("readInt()".equals(right)) {
      String tmp = "r" + (tempCounter[0]++);
      CompilerHelpers.emitReadIntTemp(tmp, decls, code);
      return Result.ok(tmp);
    }
    try {
      Integer.parseInt(right);
      return Result.ok(right);
    } catch (NumberFormatException nfe) {
      if (!kinds.containsKey(right)) {
        return Result.err(new CompileError("unknown RHS in assignment: " + right, source));
      }
      if (!"i32".equals(kinds.get(right))) {
        return Result.err(new CompileError("type mismatch in assignment", source));
      }
      return Result.ok(right);
    }
  }
}
