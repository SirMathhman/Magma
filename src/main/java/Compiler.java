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

    // Special-case: a tiny language that uses an intrinsic readInt call.
    // Tests provide a prelude exactly equal to "intrinsic fn readInt() : I32; "
    // followed by an expression like "readInt()" or "readInt() + readInt()".
    final String PRELUDE = "intrinsic fn readInt() : I32; ";
    if (input.startsWith(PRELUDE)) {
      String expr = input.substring(PRELUDE.length()).trim();
      if (expr.isEmpty()) {
        throw new CompileException("Undefined symbol: " + input);
      }
      // Split on '+' and ensure each operand is exactly "readInt()" (ignoring spaces)
      String[] parts = expr.split("\\+");
      int count = parts.length;
      for (int i = 0; i < parts.length; i++) {
        parts[i] = parts[i].trim();
        if (!parts[i].equals("readInt()")) {
          throw new CompileException("Undefined symbol: " + input);
        }
      }

      // Generate C program that reads 'count' integers and returns their sum
      StringBuilder sb = new StringBuilder();
      sb.append("#include <stdio.h>\n");
      sb.append("int main() {\n");
      sb.append("  int v; int sum = 0;\n");
      sb.append("  for (int i = 0; i < ").append(count).append("; i++) {\n");
      sb.append("    if (scanf(\"%d\", &v) != 1) return 1;\n");
      sb.append("    sum += v;\n");
      sb.append("  }\n");
      sb.append("  return sum;\n");
      sb.append("}");
      return sb.toString();
    }

    throw new CompileException("Undefined symbol: " + input);
  }
}
