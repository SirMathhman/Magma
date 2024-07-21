package magma;

import java.util.Optional;

public class Compiler {
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final String STATEMENT_END = ";";
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";

    public static String renderImport(String name) {
        return IMPORT_KEYWORD_WITH_SPACE + name + STATEMENT_END;
    }

    static String compile(String input) throws CompileException {
        if (input.isEmpty() || input.startsWith(PACKAGE_KEYWORD_WITH_SPACE)) return "";
        return compileImport(input).orElseThrow(() -> new CompileException("Invalid input", input));
    }

    private static Optional<String> compileImport(String input) {
        if (!input.startsWith(IMPORT_KEYWORD_WITH_SPACE + "test" + STATEMENT_END)) return Optional.empty();
        var afterKeyword = input.substring(IMPORT_KEYWORD_WITH_SPACE.length());

        if (!afterKeyword.endsWith(STATEMENT_END)) return Optional.empty();
        var name = afterKeyword.substring(0, afterKeyword.length() - STATEMENT_END.length());

        return Optional.of(renderImport(name));
    }
}