package magma.app.compile;

import java.util.Optional;

import static magma.app.compile.Splitter.BLOCK_END;
import static magma.app.compile.Splitter.STATEMENT_END;

public class Compiler {
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final String EMPTY_CONTENT = " " + Splitter.BLOCK_START + Splitter.BLOCK_END;
    public static final String TRAIT_KEYWORD_WITH_SPACE = "trait ";
    public static final String INTERFACE_KEYWORD_WITH_SPACE = "interface ";
    public static final String EXPORT_KEYWORD_WITH_SPACE = "export ";
    public static final String PUBLIC_KEYWORD_WITH_SPACE = "public ";
    public static final String VOID_KEYWORD_WITH_SPACE = "void ";
    public static final String EMPTY_PARAMS = "()";
    public static final String DEFINITION_SUFFIX = " : () => Void";
    public static final String NAME = "name";
    public static final PrefixRule METHOD_RULE = new PrefixRule(VOID_KEYWORD_WITH_SPACE, new FirstRule(new ExtractRule(NAME), EMPTY_PARAMS, new ExtractRule("content")));
    public static final SuffixRule DEFINITION_RULE = new SuffixRule(new ExtractRule(NAME), DEFINITION_SUFFIX);
    public static final String MODIFIERS = "modifiers";
    public static final String MEMBERS = "members";

    private static String compileRootMember(String input) throws CompileException {
        if (input.isEmpty()) return "";
        return compilePackage(input)
                .or(() -> compileImport(input))
                .or(() -> compileInterface(input))
                .orElseThrow(() -> new CompileException("Invalid root member", input));
    }

    private static Optional<String> compilePackage(String input) {
        return input.startsWith(PACKAGE_KEYWORD_WITH_SPACE) ? Optional.of("") : Optional.empty();
    }

    private static Optional<String> compileImport(String input) {
        if (input.startsWith(IMPORT_KEYWORD_WITH_SPACE)) return Optional.of(input);
        return Optional.empty();
    }

    private static Optional<String> compileInterface(String input) {
        var keywordIndex = input.indexOf(INTERFACE_KEYWORD_WITH_SPACE);
        if (keywordIndex == -1) return Optional.empty();

        var oldModifiers = input.substring(0, keywordIndex);
        var node2 = new Node().withString(MODIFIERS, oldModifiers);

        var after = input.substring(keywordIndex + INTERFACE_KEYWORD_WITH_SPACE.length());
        var contentIndex = after.indexOf(Splitter.BLOCK_START);
        if (contentIndex == -1) return Optional.empty();

        var name = after.substring(0, contentIndex).strip();
        var node1 = node2.withString(NAME, name);

        var afterBlockStart = after.substring(contentIndex + 1);
        if (!afterBlockStart.endsWith(String.valueOf(Splitter.BLOCK_END))) return Optional.empty();

        var inputMember = afterBlockStart.substring(0, afterBlockStart.length() - 1);
        var outputMember = compileMethod(inputMember);
        var node = node1.withString(MEMBERS, outputMember);

        var modified = node.mapString(MODIFIERS, modifiers -> modifiers.equals(PUBLIC_KEYWORD_WITH_SPACE) ? EXPORT_KEYWORD_WITH_SPACE : "");

        return createTraitRule().generate(modified);
    }

    private static String compileMethod(String inputMember) {
        return METHOD_RULE.parse(inputMember).flatMap(DEFINITION_RULE::generate).orElse("");
    }

    static Rule createTraitRule() {
        var modifiers = new ExtractRule(MODIFIERS);
        var name = new ExtractRule(NAME);
        var members = new ExtractRule(MEMBERS);
        var content = new SuffixRule(members, String.valueOf(BLOCK_END));
        var afterKeyword = new FirstRule(name, String.valueOf(Splitter.BLOCK_START), content);
        return new FirstRule(modifiers, TRAIT_KEYWORD_WITH_SPACE, afterKeyword);
    }

    static String renderMethod(String name) {
        return VOID_KEYWORD_WITH_SPACE + name + EMPTY_PARAMS + STATEMENT_END;
    }

    public String compile(String input) throws CompileException {
        var rootMembers = new Splitter(input).split().toList();

        var output = new StringBuilder();
        for (var rootMember : rootMembers) {
            var stripped = rootMember.strip();
            if (stripped.isEmpty()) continue;
            output.append(compileRootMember(stripped));
        }

        return output.toString();
    }
}