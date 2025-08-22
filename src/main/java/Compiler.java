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
        // reads N integers from stdin (where N is the number of occurrences of
        // readInt()) and returns their sum as the process exit code.
        final String prelude = "extern fn readInt() : I32;";
        if (input != null && input.startsWith(prelude)) {
            String expr = input.substring(prelude.length()).trim();
            // count occurrences of readInt()
            int count = 0;
            int idx = 0;
            while ((idx = expr.indexOf("readInt()", idx)) != -1) {
                count++;
                idx += "readInt()".length();
            }

            if (count <= 0) {
                throw new CompileException("No readInt() calls found in input.");
            }

            StringBuilder sb = new StringBuilder();
            sb.append("#include <stdio.h>\n");
            sb.append("#include <stdlib.h>\n");
            sb.append("int main(void) {\n");
            sb.append("    int x = 0;\n");
            sb.append("    int sum = 0;\n");
            sb.append("    for (int i = 0; i < ").append(count).append("; ++i) {\n");
            sb.append("        if (scanf(\"%d\", &x) != 1) return 1;\n");
            sb.append("        sum += x;\n");
            sb.append("    }\n");
            sb.append("    return sum;\n");
            sb.append("}\n");
            return sb.toString();
        }

        throw new CompileException("Compilation always fails in this demo.");
    }
}
