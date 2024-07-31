package magma;

import java.util.Optional;

public class Compiler {
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final String CLASS_KEYWORD_WITH_SPACE = "class ";
    public static final String EMPTY_CONTENT = " {}";
    public static final String PUBLIC_KEYWORD_WITH_SPACE = "public ";
    public static final String EXPORT_KEYWORD_WITH_SPACE = "export ";

    static String renderPackage(String name) {
        return PACKAGE_KEYWORD_WITH_SPACE + name + Splitter.STATEMENT_END;
    }

    static String renderImport(String whitespace, String parent, String child) {
        return whitespace + IMPORT_KEYWORD_WITH_SPACE + parent + "." + child + Splitter.STATEMENT_END;
    }

    private static String compileRootMember(String input) throws ParseException {
        if (input.isEmpty() || input.startsWith(PACKAGE_KEYWORD_WITH_SPACE)) return "";

        if (input.startsWith(IMPORT_KEYWORD_WITH_SPACE)) return input;

        return compileClass(input).orElseThrow(() -> new ParseException("Invalid root", input));
    }

    private static Optional<String> compileClass(String input) {
        var classIndex = input.indexOf(CLASS_KEYWORD_WITH_SPACE);
        if (classIndex == -1) return Optional.empty();

        var oldModifiers = input.substring(0, classIndex);
        var truncatedRight = input.substring(classIndex + CLASS_KEYWORD_WITH_SPACE.length());
        if (!truncatedRight.endsWith(EMPTY_CONTENT)) return Optional.empty();

        var name = truncatedRight.substring(0, truncatedRight.length() - EMPTY_CONTENT.length());
        var newModifiers = oldModifiers.equals(PUBLIC_KEYWORD_WITH_SPACE) ? EXPORT_KEYWORD_WITH_SPACE : "";
        return Optional.of(renderFunction(newModifiers, name));
    }

    static String renderFunction(String modifiers, String name) {
        return modifiers + CLASS_KEYWORD_WITH_SPACE + "def " + name + "() =>" + EMPTY_CONTENT;
    }

    static String renderClass(String modifiers, String name) {
        return modifiers + CLASS_KEYWORD_WITH_SPACE + name + EMPTY_CONTENT;
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