package magma;

public class Compiler {

    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final String STATEMENT_END = ";";

    static String renderPackage(String name) {
        return PACKAGE_KEYWORD_WITH_SPACE + name + STATEMENT_END;
    }

    String compile(String input) throws ParseException {
        if (input.isEmpty() || input.startsWith(PACKAGE_KEYWORD_WITH_SPACE)) return "";

        throw new ParseException("Invalid root", input);
    }
}