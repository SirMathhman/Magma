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
        return truncateLeft(input, INTERFACE_KEYWORD_WITH_SPACE)
                .flatMap(nameAndContent -> truncateRight(nameAndContent, EMPTY_CONTENT))
                .map(Compiler::renderTrait);
    }

    private static Optional<String> truncateLeft(String input, String slice) {
        if (!input.startsWith(slice)) return Optional.empty();
        return Optional.of(input.substring(slice.length()));
    }

    private static Optional<String> truncateRight(String input, String slice) {
        if (!input.endsWith(slice)) return Optional.empty();
        return Optional.of(input.substring(0, input.length() - slice.length()));
    }

    private static Optional<String> compileImport(String input) {
        return truncateLeft(input, IMPORT_KEYWORD_WITH_SPACE)
                .flatMap(afterKeyword -> truncateRight(afterKeyword, String.valueOf(STATEMENT_END)))
                .map(Compiler::renderImport);
    }

    static String renderTrait(String name) {
        return TRAIT_KEYWORD_WITH_SPACE + name + EMPTY_CONTENT;
    }

    static String renderInterface(String name) {
        return INTERFACE_KEYWORD_WITH_SPACE + name + EMPTY_CONTENT;
    }
}