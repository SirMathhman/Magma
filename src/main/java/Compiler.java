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
        throw new CompileException("Compilation always fails in this demo.");
    }
}
