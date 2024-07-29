package magma.app.compile;

import java.util.ArrayList;
import java.util.List;

import static magma.app.compile.Splitter.BLOCK_END;
import static magma.app.compile.Splitter.BLOCK_START;
import static magma.app.compile.Splitter.STATEMENT_END;

public class Compiler {
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final String TRAIT_KEYWORD_WITH_SPACE = "trait ";
    public static final String INTERFACE_KEYWORD_WITH_SPACE = "interface ";
    public static final String EXPORT_KEYWORD_WITH_SPACE = "export ";
    public static final String PUBLIC_KEYWORD_WITH_SPACE = "public ";
    public static final String VOID_KEYWORD_WITH_SPACE = "void ";
    public static final String EMPTY_PARAMS = "()";
    public static final String DEFINITION_SUFFIX = " : () => Void";
    public static final String NAME = "name";
    public static final Rule METHOD_RULE = new PrefixRule(VOID_KEYWORD_WITH_SPACE, new FirstRule(new StringRule(NAME), EMPTY_PARAMS, new StringRule("content")));
    public static final Rule DEFINITION_RULE = new SuffixRule(new StringRule(NAME), DEFINITION_SUFFIX);
    public static final String MODIFIERS = "modifiers";
    public static final String MEMBERS = "members";
    public static final String NAMESPACE = "namespace";
    public static final String PACKAGE = "package";
    public static final String IMPORT = "import";
    public static final String INTERFACE = "interface";
    public static final String TRAIT = "trait";

    private static Rule createMagmaRootRule() {
        return new DisjunctionRule(List.of(createImportRule(), createTraitRule()));
    }

    private static Rule createJavaRootRule() {
        return new DisjunctionRule(List.of(createPackageRule(),
                createImportRule(),
                createInterfaceRule()));
    }

    private static Rule createImportRule() {
        return new TypeRule(IMPORT, new PrefixRule(IMPORT_KEYWORD_WITH_SPACE, new StringRule(NAMESPACE)));
    }

    private static Rule createPackageRule() {
        return new TypeRule(PACKAGE, new PrefixRule(PACKAGE_KEYWORD_WITH_SPACE, new StringRule(NAMESPACE)));
    }

    private static Rule createInterfaceRule() {
        var modifiers = new StringRule(MODIFIERS);
        var name = new StringRule(NAME);
        var members = new SuffixRule(new OptionalNodeRule(MEMBERS, new NodeRule(MEMBERS, METHOD_RULE)), String.valueOf(BLOCK_END));
        var afterKeyword = new FirstRule(name, String.valueOf(BLOCK_START), members);
        return new TypeRule(INTERFACE, new FirstRule(modifiers, INTERFACE_KEYWORD_WITH_SPACE, afterKeyword));
    }

    private static Node modify(Node node) {
        return node.retype(TRAIT).mapString(MODIFIERS, modifiers -> modifiers.equals(PUBLIC_KEYWORD_WITH_SPACE) ? EXPORT_KEYWORD_WITH_SPACE : "");
    }

    static Rule createTraitRule() {
        var modifiers = new StringRule(MODIFIERS);
        var name = new StringRule(NAME);
        var members = new NodeRule(MEMBERS, DEFINITION_RULE);
        var content = new SuffixRule(new OptionalNodeRule(MEMBERS, members), String.valueOf(BLOCK_END));
        var afterKeyword = new FirstRule(name, String.valueOf(Splitter.BLOCK_START), content);
        return new TypeRule(TRAIT, new FirstRule(modifiers, TRAIT_KEYWORD_WITH_SPACE, afterKeyword));
    }

    static String renderMethod(String name) {
        return VOID_KEYWORD_WITH_SPACE + name + EMPTY_PARAMS + STATEMENT_END;
    }

    private static List<Node> parse(List<String> rootMembers) throws CompileException {
        var output = new ArrayList<Node>();
        for (var rootMember : rootMembers) {
            var stripped = rootMember.strip();
            if (stripped.isEmpty()) continue;

            output.add(createJavaRootRule().parse(stripped).$());
        }

        return output;
    }

    public String compile(String input) throws CompileException {
        var rootMembers = new Splitter(input).split().toList();

        var parsed = parse(rootMembers);
        var modified = new ArrayList<Node>();
        for (Node node : parsed) {
            if(node.is(PACKAGE)) continue;
            modified.add(modify(node));
        }

        return generate(modified);
    }

    private String generate(List<Node> parsed) throws CompileException {
        var builder = new StringBuilder();
        for (Node node : parsed) {
            builder.append(createMagmaRootRule().generate(node).$());
        }
        return builder.toString();
    }
}