package magma;

import java.util.Optional;

public class Compiler {
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final String CLASS_KEYWORD_WITH_SPACE = "class ";
    public static final String PUBLIC_KEYWORD_WITH_SPACE = "public ";
    public static final String EXPORT_KEYWORD_WITH_SPACE = "export ";
    public static final String VOID_KEYWORD_WITH_SPACE = "void ";
    public static final String METHOD_SUFFIX = "(){}";

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

    private static Optional<String> compileClass(String input) throws ParseException {
        var classIndex = input.indexOf(CLASS_KEYWORD_WITH_SPACE);

        if (classIndex == -1) return Optional.empty();
        var oldModifiers = input.substring(0, classIndex);
        var newModifiers = oldModifiers.equals(PUBLIC_KEYWORD_WITH_SPACE) ? EXPORT_KEYWORD_WITH_SPACE : "";
        Node node = new Node();
        var withModifiers = node.with(Node.MODIFIERS, newModifiers + CLASS_KEYWORD_WITH_SPACE);

        var truncatedRight = input.substring(classIndex + CLASS_KEYWORD_WITH_SPACE.length());
        var startIndex = truncatedRight.indexOf(Splitter.BLOCK_START);

        if (startIndex == -1) return Optional.empty();
        var name = truncatedRight.substring(0, startIndex).strip();
        var withName = withModifiers.with(Node.NAME, name);
        var contentAndEnd = truncatedRight.substring(startIndex + 1);

        if (!contentAndEnd.endsWith(String.valueOf(Splitter.BLOCK_END))) return Optional.empty();
        var content = contentAndEnd.substring(0, contentAndEnd.length() - 1);
        var compiledContent = compileClassMembers(content);
        var withContent = withName.with(Node.CONTENT, compiledContent);

        return Optional.of(renderFunction(withContent));
    }

    private static String compileClassMembers(String content) throws ParseException {
        if (content.isEmpty()) return "";
        return compileMethod(content).orElseThrow(() -> new ParseException("Unknown class member", content));
    }

    private static Optional<String> compileMethod(String content) {
        if (!content.startsWith(VOID_KEYWORD_WITH_SPACE)) return Optional.empty();
        var truncatedRight = content.substring(VOID_KEYWORD_WITH_SPACE.length());

        if (!truncatedRight.endsWith(METHOD_SUFFIX)) return Optional.empty();
        var name = truncatedRight.substring(0, truncatedRight.length() - METHOD_SUFFIX.length());
        Node node1 = new Node();
        var node = node1.with(Node.NAME, name);
        return Optional.of(renderFunction(node));
    }

    static String renderFunction(Node node) {
        var name = node.findString(Node.NAME).orElseThrow();
        var modifiers = node.findString(Node.MODIFIERS).orElse("");
        var content = node.findString(Node.CONTENT).orElse("");

        return modifiers + "def " + name + "() =>" + " " + Splitter.BLOCK_START + content + Splitter.BLOCK_END;
    }

    static String renderJavaClass(String modifiers, String name, String content) {
        return modifiers + CLASS_KEYWORD_WITH_SPACE + name + " " + Splitter.BLOCK_START + content + Splitter.BLOCK_END;
    }

    static String renderMethod(String name) {
        return VOID_KEYWORD_WITH_SPACE + name + METHOD_SUFFIX;
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