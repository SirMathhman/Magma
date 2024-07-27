package magma.app.compile;

public class Compiler {
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";

    private static String compileRootMember(String input) throws CompileException {
        if (input.isEmpty() || input.startsWith(PACKAGE_KEYWORD_WITH_SPACE)) return "";
        if (input.startsWith(IMPORT_KEYWORD_WITH_SPACE)) return input;

        throw new CompileException("Invalid root member", input);
    }

    public String compile(String input) throws CompileException {
        var rootMembers = new Splitter(input).split().toList();

        var output = new StringBuilder();
        for (var rootMember : rootMembers) {
            output.append(compileRootMember(rootMember.strip()));
        }

        return output.toString();
    }
}