package magma.app.compile;

import java.util.Optional;

public class Compiler {
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final String EMPTY_CONTENT = " {}";
    public static final String TRAIT_KEYWORD_WITH_SPACE = "trait ";
    public static final String INTERFACE_KEYWORD_WITH_SPACE = "interface ";
    public static final String EXPORT_KEYWORD_WITH_SPACE = "export ";
    public static final String PUBLIC_KEYWORD_WITH_SPACE = "public ";

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
        var after = input.substring(keywordIndex + INTERFACE_KEYWORD_WITH_SPACE.length());

        var contentIndex = after.indexOf(EMPTY_CONTENT);
        if (contentIndex == -1) return Optional.empty();
        var name = after.substring(0, contentIndex);

        var newModifiers = oldModifiers.equals(PUBLIC_KEYWORD_WITH_SPACE) ? EXPORT_KEYWORD_WITH_SPACE : "";
        return Optional.of(renderTrait(newModifiers, name));
    }

    static String renderTrait(String modifiers, String name) {
        return modifiers + TRAIT_KEYWORD_WITH_SPACE + name + EMPTY_CONTENT;
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