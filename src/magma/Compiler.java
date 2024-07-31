package magma;

import java.util.List;
import java.util.Optional;

public class Compiler {
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final String CLASS_KEYWORD_WITH_SPACE = "class ";
    public static final String PUBLIC_KEYWORD_WITH_SPACE = "public ";
    public static final String EXPORT_KEYWORD_WITH_SPACE = "export ";
    public static final String VOID_KEYWORD_WITH_SPACE = "void ";
    public static final String METHOD_SUFFIX = "(){}";
    public static final String METHOD = "method";
    public static final String MODIFIERS = "modifiers";
    public static final String NAME = "name";
    public static final Rule METHOD_RULE = createMethodRule();
    public static final DisjunctionRule CLASS_MEMBERS_RULE = new DisjunctionRule(List.of(
            new TypeRule("empty", EmptyRule.EMPTY_RULE),
            METHOD_RULE
    ));
    public static final String CONTENT = "content";

    private static Rule createMethodRule() {
        return new TypeRule(METHOD, new PrefixRule(new SuffixRule(new StringRule(NAME), METHOD_SUFFIX), VOID_KEYWORD_WITH_SPACE));
    }

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
        var withModifiers = new Node().withString(MODIFIERS, newModifiers + CLASS_KEYWORD_WITH_SPACE);

        var truncatedRight = input.substring(classIndex + CLASS_KEYWORD_WITH_SPACE.length());
        var startIndex = truncatedRight.indexOf(Splitter.BLOCK_START);

        if (startIndex == -1) return Optional.empty();
        var name = truncatedRight.substring(0, startIndex).strip();
        var other1 = new Node().withString(NAME, name);
        var contentAndEnd = truncatedRight.substring(startIndex + 1);

        if (!contentAndEnd.endsWith(String.valueOf(Splitter.BLOCK_END))) return Optional.empty();
        var content = contentAndEnd.substring(0, contentAndEnd.length() - 1);

        var withName = withModifiers.merge(other1);
        var other = new NodeRule(CLASS_MEMBERS_RULE, CONTENT).parse(content);
        if (other.isEmpty()) return Optional.empty();
        var withContent = withName.merge(other.orElseThrow());
        return Optional.of(renderFunction(withContent).orElseThrow());
    }

    private static Optional<String> renderStatement(Node node) {
        return node.is(METHOD) ? renderFunction(node) : Optional.of("");
    }

    static Optional<String> renderFunction(Node node) {
        var nameOptional = node.findString(NAME);
        if (nameOptional.isEmpty()) return Optional.empty();

        var name = nameOptional.orElseThrow();
        var modifiers = node.findString(MODIFIERS).orElse("");

        var content = node.findNode(CONTENT)
                .flatMap(Compiler::renderStatement)
                .orElse("");

        return Optional.of(modifiers + "def " + name + "() =>" + " " + Splitter.BLOCK_START + content + Splitter.BLOCK_END);
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