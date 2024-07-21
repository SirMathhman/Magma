package magma.app.compile;

import magma.app.compile.rule.ExtractRule;
import magma.app.compile.rule.Rule;

import java.util.Map;
import java.util.Optional;

public class Compiler {
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final char STATEMENT_END = Splitter.STATEMENT_END;
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final String TRAIT_KEYWORD_WITH_SPACE = "trait ";
    public static final String EMPTY_CONTENT = " {}";
    public static final String INTERFACE_KEYWORD_WITH_SPACE = "interface ";
    public static final String PUBLIC_KEYWORD_WITH_SPACE = "public ";
    public static final String EXPORT_KEYWORD_WITH_SPACE = "export ";
    public static final String MODIFIERS = "modifiers";
    public static final String NAME = "name";
    public static final Rule INTERFACE_RULE = createInterfaceRule();

    private static FirstRule createInterfaceRule() {
        var modifiers = new ExtractRule(MODIFIERS);
        var name = new ExtractRule(NAME);

        return new FirstRule(modifiers, new RightRule(name, EMPTY_CONTENT), INTERFACE_KEYWORD_WITH_SPACE);
    }

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
                .or(() -> compileInterface(input))
                .orElseThrow(() -> new CompileException("Invalid input", input));
    }

    private static Optional<String> compileInterface(String input) {
        return INTERFACE_RULE.parse(input)
                .map(map -> {
                    var oldModifiers = map.get(MODIFIERS);
                    var newModifiers = oldModifiers.equals(PUBLIC_KEYWORD_WITH_SPACE) ? EXPORT_KEYWORD_WITH_SPACE : "";
                    map.put(MODIFIERS, newModifiers);
                    return map;
                })
                .map(Compiler::renderTrait);
    }

    private static Optional<String> truncateLeft(String slice, String input) {
        if (!input.startsWith(slice)) return Optional.empty();
        return Optional.of(input.substring(slice.length()));
    }

    private static Optional<String> compileImport(String input) {
        return truncateLeft(IMPORT_KEYWORD_WITH_SPACE, input)
                .flatMap(afterKeyword -> RightRule.truncateRight(afterKeyword, String.valueOf(STATEMENT_END)))
                .map(Compiler::renderImport);
    }

    static String renderTrait(Map<String, String> node) {
        var modifiers0 = node.getOrDefault(MODIFIERS, "");
        var name0 = node.get(NAME);
        return modifiers0 + TRAIT_KEYWORD_WITH_SPACE + name0 + EMPTY_CONTENT;
    }

    static String renderInterface(String name) {
        return renderInterface("", name);
    }

    static String renderInterface(String modifiers, String name) {
        return modifiers + INTERFACE_KEYWORD_WITH_SPACE + name + EMPTY_CONTENT;
    }
}