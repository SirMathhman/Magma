package magma;

public class Compiler {
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final String CLASS_KEYWORD_WITH_SPACE = "class ";
    public static final String EMPTY_CONTENT = " {}";

    static String renderPackage(String name) {
        return PACKAGE_KEYWORD_WITH_SPACE + name + Splitter.STATEMENT_END;
    }

    static String renderImport(String whitespace, String parent, String child) {
        return whitespace + IMPORT_KEYWORD_WITH_SPACE + parent + "." + child + Splitter.STATEMENT_END;
    }

    private static String compileRootMember(String input) throws ParseException {
        if (input.isEmpty() || input.startsWith(PACKAGE_KEYWORD_WITH_SPACE)) return "";
        if (input.startsWith(IMPORT_KEYWORD_WITH_SPACE)) return input;
        if(input.startsWith(CLASS_KEYWORD_WITH_SPACE) && input.endsWith(EMPTY_CONTENT)) {
            var name = input.substring(CLASS_KEYWORD_WITH_SPACE.length(), input.length() - EMPTY_CONTENT.length());
            return renderFunction(name);
        }

        throw new ParseException("Invalid root", input);
    }

    static String renderFunction(String name) {
        return CLASS_KEYWORD_WITH_SPACE + "def " + name + "() =>" + EMPTY_CONTENT;
    }

    static String renderClass(String name) {
        return CLASS_KEYWORD_WITH_SPACE + name + EMPTY_CONTENT;
    }

    String compile(String input) throws ParseException {
        var rootMembers = Splitter.splitRootMembers(input);
        var output = new StringBuilder();
        for (var rootMember : rootMembers) {
            var stripped = rootMember.strip();
            var compiled = compileRootMember(stripped);
            output.append(compiled);
        }

        return output.toString();
    }

}