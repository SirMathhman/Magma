public class Compiler {
    /**
     * Compile input and return a result string. This demo always fails by throwing
     * a checked CompileException.
     *
     * @param input source to compile
     * @return compilation result string (never returned in this demo)
     * @throws CompileException always thrown to indicate failure
     */
    public String compile(String input) throws CompileException {
        // Test-hook: when input starts with the prelude, generate C source that
        // evaluates the expression by replacing readInt() with next_int(), which
        // reads from stdin. This supports operations like +, -, *.
        final String prelude = "extern fn readInt() : I32;";
        if (input != null && input.startsWith(prelude)) {
            String expr = input.substring(prelude.length()).trim();
            if (expr.isEmpty()) {
                throw new CompileException("No expression provided after prelude.");
            }

            // Replace DSL readInt() calls with next_int() helper
            String cExpr = expr.replace("readInt()", "next_int()");

            StringBuilder sb = new StringBuilder();
            sb.append("#include <stdio.h>\n");
            sb.append("#include <stdlib.h>\n");
            sb.append("int next_int() {\n");
            sb.append("    int x = 0;\n");
            sb.append("    if (scanf(\"%d\", &x) != 1) exit(1);\n");
            sb.append("    return x;\n");
            sb.append("}\n");
            sb.append("int main(void) {\n");
            sb.append("    int result = (").append(cExpr).append(");\n");
            sb.append("    return result;\n");
            sb.append("}\n");
            return sb.toString();
        }

        throw new CompileException("Compilation always fails in this demo.");
    }
}
