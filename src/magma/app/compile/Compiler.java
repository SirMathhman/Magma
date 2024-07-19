package magma.app.compile;

import magma.app.ApplicationException;

import java.util.Optional;

public class Compiler {
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final String IMPORT_SEPARATOR = ".";
    public static final String STATEMENT_END = ";";
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";

    public static String compile(String input) throws ApplicationException {
        if (input.isEmpty()) return "";
        return compilePackage(input)
                .or(() -> compileImport(input))
                .orElseThrow(() -> new ApplicationException("Unknown input: " + input));
    }

    private static Optional<String> compilePackage(String input) {
        return input.startsWith(PACKAGE_KEYWORD_WITH_SPACE)
                ? Optional.of("")
                : Optional.empty();
    }

    private static Optional<String> compileImport(String input) {
        if (!input.startsWith(IMPORT_KEYWORD_WITH_SPACE)) return Optional.empty();
        var afterKeyword = input.substring(IMPORT_KEYWORD_WITH_SPACE.length());

        if (!afterKeyword.endsWith(STATEMENT_END)) return Optional.empty();
        return Optional.of(input);
    }

    public static String renderImport(String parent, String child) {
        return IMPORT_KEYWORD_WITH_SPACE + parent + IMPORT_SEPARATOR + child + STATEMENT_END;
    }
}
