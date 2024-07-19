package magma.app.compile;

import magma.app.ApplicationException;

import java.util.Optional;

public class Compiler {
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final String IMPORT_SEPARATOR = ".";
    public static final String STATEMENT_END = ";";
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final String CLASS_KEYWORD_WITH_SPACE = "class ";
    public static final String EMPTY_BLOCK = " {}";

    public static String compile(String input) throws ApplicationException {
        var lines = Splitter.split(input).toList();

        var builder = new StringBuilder();
        for (String line : lines) {
            builder.append(compileLine(line.strip()));
        }

        return builder.toString();
    }

    private static String compileLine(String input) throws ApplicationException {
        return compilePackage(input)
                .or(() -> compileImport(input))
                .or(() -> compileClass(input))
                .orElseThrow(() -> new ApplicationException("Unknown input: " + input));
    }

    private static Optional<String> compileClass(String input) {
        if (!input.startsWith(CLASS_KEYWORD_WITH_SPACE)) return Optional.empty();
        var afterKeyword = input.substring(CLASS_KEYWORD_WITH_SPACE.length());

        if (!afterKeyword.endsWith(EMPTY_BLOCK)) return Optional.empty();
        var name = afterKeyword.substring(0, afterKeyword.length() - EMPTY_BLOCK.length());

        return Optional.of(renderFunction(name));

    }

    private static Optional<String> compilePackage(String input) {
        return input.startsWith(PACKAGE_KEYWORD_WITH_SPACE) ? Optional.of("") : Optional.empty();
    }

    private static Optional<String> compileImport(String input) {
        if (!input.startsWith(IMPORT_KEYWORD_WITH_SPACE)) return Optional.empty();
        var afterKeyword = input.substring(IMPORT_KEYWORD_WITH_SPACE.length());

        if (!afterKeyword.endsWith(STATEMENT_END)) return Optional.empty();
        return Optional.of(input);
    }

    public static String renderImport(String leftPadding, String parent, String child) {
        return leftPadding + IMPORT_KEYWORD_WITH_SPACE + parent + IMPORT_SEPARATOR + child + STATEMENT_END;
    }

    public static String renderPackage(String name) {
        return PACKAGE_KEYWORD_WITH_SPACE + name + STATEMENT_END;
    }

    static String renderFunction(String name) {
        return CLASS_KEYWORD_WITH_SPACE + "def " + name + "() =>" + EMPTY_BLOCK;
    }

    static String renderClass(String name) {
        return CLASS_KEYWORD_WITH_SPACE + name + EMPTY_BLOCK;
    }
}
