package magma.app.compile;

import magma.api.Tuple;

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
        return splitLeft(INTERFACE_KEYWORD_WITH_SPACE, input).flatMap(tuple -> truncateRight(tuple.right(), EMPTY_CONTENT).map(name -> {
            var oldModifiers = tuple.left().equals(PUBLIC_KEYWORD_WITH_SPACE) ? EXPORT_KEYWORD_WITH_SPACE : "";
            return renderTrait(oldModifiers, name);
        }));
    }

    private static Optional<Tuple<String, String>> splitLeft(String slice, String input) {
        var index = input.indexOf(slice);
        if (index == -1) return Optional.empty();
        var left = input.substring(0, index);
        var right = input.substring(index + slice.length());
        return Optional.of(new Tuple<>(left, right));
    }

    private static Optional<String> truncateLeft(String slice, String input) {
        if (!input.startsWith(slice)) return Optional.empty();
        return Optional.of(input.substring(slice.length()));
    }

    private static Optional<String> truncateRight(String input, String slice) {
        if (!input.endsWith(slice)) return Optional.empty();
        return Optional.of(input.substring(0, input.length() - slice.length()));
    }

    private static Optional<String> compileImport(String input) {
        return truncateLeft(IMPORT_KEYWORD_WITH_SPACE, input)
                .flatMap(afterKeyword -> truncateRight(afterKeyword, String.valueOf(STATEMENT_END)))
                .map(Compiler::renderImport);
    }

    static String renderTrait(String name) {
        return renderTrait("", name);
    }

    static String renderTrait(String modifiers, String name) {
        return modifiers + TRAIT_KEYWORD_WITH_SPACE + name + EMPTY_CONTENT;
    }

    static String renderInterface(String name) {
        return renderInterface("", name);
    }

    static String renderInterface(String modifiers, String name) {
        return modifiers + INTERFACE_KEYWORD_WITH_SPACE + name + EMPTY_CONTENT;
    }
}