package magma.app.compile;

import java.util.Optional;

public class Compiler {
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final char STATEMENT_END = Splitter.STATEMENT_END;
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final String TRAIT_KEYWORD_WITH_SPACE = "trait ";
    public static final String EMPTY_CONTENT = " {}";
    public static final String INTERFACE_KEYWORD_WITH_SPACE = "interface ";

    public static String renderImport(String name) {
        return renderImport("", name);
    }

    public static String renderImport(String leading, String name) {
        return leading + IMPORT_KEYWORD_WITH_SPACE + name + STATEMENT_END;
    }

    public static String compile(String input) throws CompileException {
        var segments = Splitter.split(input);

        var output = new StringBuilder();
        for (var line : segments) {
            output.append(compileRootMember(line.strip()));
        }
        return output.toString();
    }

    private static String compileRootMember(String input) throws CompileException {
        if (input.isEmpty() || input.startsWith(PACKAGE_KEYWORD_WITH_SPACE)) return "";
        return compileImport(input)
                .or(() -> compileTrait(input))
                .orElseThrow(() -> new CompileException("Invalid input", input));
    }

    private static Optional<String> compileTrait(String input) {
        if (!input.startsWith(INTERFACE_KEYWORD_WITH_SPACE)) return Optional.empty();
        var nameAndContent = input.substring(INTERFACE_KEYWORD_WITH_SPACE.length());

        if (!nameAndContent.endsWith(EMPTY_CONTENT)) return Optional.empty();
        var name = nameAndContent.substring(0, nameAndContent.length() - EMPTY_CONTENT.length());

        return Optional.of(renderTrait(name));
    }

    private static Optional<String> compileImport(String input) {
        if (!input.startsWith(IMPORT_KEYWORD_WITH_SPACE)) return Optional.empty();
        var afterKeyword = input.substring(IMPORT_KEYWORD_WITH_SPACE.length());

        if (afterKeyword.isEmpty() || afterKeyword.charAt(afterKeyword.length() - 1) != STATEMENT_END)
            return Optional.empty();

        var name = afterKeyword.substring(0, afterKeyword.length() - 1);
        return Optional.of(renderImport(name));
    }

    static String renderTrait(String name) {
        return TRAIT_KEYWORD_WITH_SPACE + name + EMPTY_CONTENT;
    }

    static String renderInterface(String name) {
        return INTERFACE_KEYWORD_WITH_SPACE + name + EMPTY_CONTENT;
    }
}