package magma;

public class CompilerDump {
  // Non-void helper to satisfy project checkstyle rules
  public static Result<String, CompileError> dump() {
    String src = "intrinsic fn readInt() : I32; readInt()";
    return Compiler.compile(src);
  }
}
