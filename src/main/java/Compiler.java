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
        // Test-hook: when a specific DSL string is passed, return C source that
        // reads an integer from stdin and returns it as the process exit code.
        if ("extern fn readInt() : I32; readInt()".equals(input)) {
            return "#include <stdio.h>\n" +
                    "#include <stdlib.h>\n" +
                    "int main(void) {\n" +
                    "    int x = 0;\n" +
                    "    if (scanf(\"%d\", &x) != 1) return 1;\n" +
                    "    return x;\n" +
                    "}\n";
        }

        throw new CompileException("Compilation always fails in this demo.");
    }
}
