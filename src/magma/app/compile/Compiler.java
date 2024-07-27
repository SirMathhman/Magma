package magma.app.compile;

import java.util.Optional;

public class Compiler {
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final String EMPTY_CONTENT = " {}";
    public static final String TRAIT_KEYWORD_WITH_SPACE = "trait ";
    public static final String INTERFACE_KEYWORD_WITH_SPACE = "interface ";

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
        if (!input.startsWith(INTERFACE_KEYWORD_WITH_SPACE)) return Optional.empty();
        var after = input.substring(INTERFACE_KEYWORD_WITH_SPACE.length());

        var contentIndex = after.indexOf(EMPTY_CONTENT);
        if (contentIndex == -1) return Optional.empty();

        var name = after.substring(0, contentIndex);
        return Optional.of(renderTrait(name));
    }

    static String renderTrait(String name) {
        return TRAIT_KEYWORD_WITH_SPACE + name + EMPTY_CONTENT;
    }

    public String compile(String input) throws CompileException {
        var rootMembers = new Splitter(input).split().toList();

        var output = new StringBuilder();
        for (var rootMember : rootMembers) {
            var stripped = rootMember.strip();
            if(stripped.isEmpty()) continue;
            output.append(compileRootMember(stripped));
        }

        return output.toString();
    }
}