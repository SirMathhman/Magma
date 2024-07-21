package magma;

public class Compiler {
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";

    static String compile(String input) throws CompileException {
        if (input.isEmpty() || input.startsWith(PACKAGE_KEYWORD_WITH_SPACE)) return "";
        else throw new CompileException("Invalid input", input);
    }
}