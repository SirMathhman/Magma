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

            // Handle a simple 'let' binding at the start of the expression, e.g.
            // "let x = readInt(); x" -> emit C that initializes x then evaluates the rest.
            StringBuilder sb = new StringBuilder();
            sb.append("#include <stdio.h>\n");
            sb.append("#include <stdlib.h>\n");
            sb.append("int next_int() {\n");
            sb.append("    int x = 0;\n");
            sb.append("    if (scanf(\"%d\", &x) != 1) exit(1);\n");
            sb.append("    return x;\n");
            sb.append("}\n");

            if (expr.startsWith("let ")) {
                // parse: let <ident> = <init>; <rest>
                int eq = expr.indexOf('=');
                int semi = expr.indexOf(';', eq);
                if (eq == -1 || semi == -1) {
                    throw new CompileException("Malformed let expression");
                }
                String ident = expr.substring(4, eq).trim();
                String initExpr = expr.substring(eq + 1, semi).trim();
                String restExpr = expr.substring(semi + 1).trim();

                String cInitExpr = initExpr.replace("readInt()", "next_int()");
                String cRestExpr = restExpr.replace("readInt()", "next_int()");

                sb.append("int main(void) {\n");
                sb.append("    int ").append(ident).append(" = (").append(cInitExpr).append(");\n");
                sb.append("    int result = (").append(cRestExpr).append(");\n");
                sb.append("    return result;\n");
                sb.append("}\n");
                return sb.toString();
            }

            // Replace DSL readInt() calls with next_int() helper for simple expressions
            String cExpr = expr.replace("readInt()", "next_int()");

            sb.append("int main(void) {\n");
            sb.append("    int result = (").append(cExpr).append(");\n");
            sb.append("    return result;\n");
            sb.append("}\n");
            return sb.toString();
        }

        throw new CompileException("Compilation always fails in this demo.");
    }
}
