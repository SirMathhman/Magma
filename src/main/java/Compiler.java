public class Compiler {
  public static String compile(String input) throws CompileException {
    if (input == null) {
      throw new CompileException("Input is undefined");
    }
    // If the input is a non-negative integer, produce a tiny C program that
    // prints the value and exits successfully. For any other input, fail.
    if (input.matches("\\d+")) {
      // Escape double quotes just in case
      String safe = input.replace("\"", "\\\"");
      return "#include <stdio.h>\nint main() { printf(\"%s\", \"" + safe + "\"); return 0; }";
    }
    throw new CompileException("Undefined symbol: " + input);
  }
}
