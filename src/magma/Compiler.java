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
    public static final String CLASS = "class";
    public static final String FUNCTION = "function";
    public static final String SEGMENTS = "segments";
    public static final String IMPORT = "import";

    private static Rule createMethodRule() {
        return new TypeRule(METHOD, new PrefixRule(VOID_KEYWORD_WITH_SPACE, new SuffixRule(new StringRule(NAME), METHOD_SUFFIX)));
    }

    static String renderPackage(String name) {
        return PACKAGE_KEYWORD_WITH_SPACE + name + Splitter.STATEMENT_END;
    }

    static String renderImport(String whitespace, String parent, String child) {
        return whitespace + IMPORT_KEYWORD_WITH_SPACE + parent + "." + child + Splitter.STATEMENT_END;
    }

    private static String compileRootMember(String input) throws ParseException {
        if (input.startsWith(PACKAGE_KEYWORD_WITH_SPACE)) return "";

        return new DisjunctionRule(List.of(createImportRule(), createClassRule()))
                .parse(input)
                .map(Compiler::modify)
                .flatMap(Compiler::render).orElseThrow(() -> new ParseException("Invalid root", input));
    }

    private static Rule createImportRule() {
        var segments = new StringRule(SEGMENTS);
        var afterKeyword = new SuffixRule(segments, String.valueOf(Splitter.STATEMENT_END));
        return new TypeRule(IMPORT, new PrefixRule(IMPORT_KEYWORD_WITH_SPACE, afterKeyword));
    }

    private static Optional<String> render(Node node) {
        if (node.is(FUNCTION)) return renderFunction(node);
        if (node.is(IMPORT)) return createImportRule().generate(node);
        return Optional.empty();
    }

    private static Rule createClassRule() {
        var modifiers = new StringRule(MODIFIERS);
        var name = new StripRule(new StringRule(NAME));
        var content = new NodeRule(CLASS_MEMBERS_RULE, CONTENT);

        var contentAndEnd = new SuffixRule(content, String.valueOf(Splitter.BLOCK_END));
        var afterKeyword = new FirstRule(name, String.valueOf(Splitter.BLOCK_START), contentAndEnd);
        return new TypeRule(CLASS, new FirstRule(modifiers, CLASS_KEYWORD_WITH_SPACE, afterKeyword));
    }

    private static Node modify(Node node) {
        if (node.is(CLASS)) {
            return node.retype(FUNCTION).mapString(MODIFIERS, oldModifiers -> {
                var newAccessor = oldModifiers.equals(PUBLIC_KEYWORD_WITH_SPACE) ? EXPORT_KEYWORD_WITH_SPACE : "";
                return newAccessor + CLASS_KEYWORD_WITH_SPACE;
            });
        }
        return node;
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
            if (stripped.isEmpty()) continue;

            var compiled = compileRootMember(stripped);
            output.append(compiled);
        }

        return output.toString();
    }

}