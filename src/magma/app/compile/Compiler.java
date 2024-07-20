package magma.app.compile;

import magma.api.Tuple;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.ApplicationException;

import java.util.List;
import java.util.Optional;

public class Compiler {
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final String IMPORT_SEPARATOR = ".";
    public static final String STATEMENT_END = ";";
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final String CLASS_KEYWORD_WITH_SPACE = "class ";
    public static final String BLOCK_START = "{";
    public static final String BLOCK_END = "}";
    public static final String PUBLIC_KEYWORD_WITH_SPACE = "public ";
    public static final String EXPORT_KEYWORD_WITH_SPACE = "export ";

    private static String renderBlock(String content) {
        return BLOCK_START + content + BLOCK_END;
    }

    public static Result<String, ApplicationException> compile(String input) {
        var lines = Splitter.split(input).toList();

        Result<StringBuilder, ApplicationException> builder = new Ok<>(new StringBuilder());
        for (String line : lines) {
            builder = builder.and(() -> compileLine(line.strip())).mapValue(tuple -> tuple.left().append(tuple.right()));
        }

        return builder.mapValue(StringBuilder::toString);
    }

    private static Result<String, ApplicationException> compileLine(String input) {
        return compilePackage(input).or(() -> compileImport(input)).or(() -> compileClass(input)).orElseGet(() -> new Err<>(new ApplicationException("Unknown input: " + input)));
    }

    private static Optional<Result<String, ApplicationException>> compileClass(String input) {
        return split(input, CLASS_KEYWORD_WITH_SPACE).flatMap(tuple -> {
            var oldModifiers = tuple.left();
            return split(tuple.right(), BLOCK_START).flatMap(tuple0 -> {
                var name = tuple0.left();
                var afterBlockStart = tuple0.right();

                if (!afterBlockStart.endsWith(BLOCK_END)) return Optional.empty();
                var inputContent = afterBlockStart.substring(0, afterBlockStart.length() - 1);

                var classMembers = Splitter.split(inputContent).toList();
                var outputContent = compileContent(classMembers);

                var newModifiers = oldModifiers.equals(PUBLIC_KEYWORD_WITH_SPACE) ? EXPORT_KEYWORD_WITH_SPACE : "";

                return Optional.of(outputContent.mapValue(content -> renderFunction(newModifiers, name, content.toString())));
            });
        });
    }

    private static Optional<Tuple<String, String>> split(String input, String slice) {
        var classIndex = input.indexOf(slice);
        if (classIndex == -1) return Optional.empty();
        var leftSlice = input.substring(0, classIndex);
        var rightSlice = input.substring(classIndex + slice.length());
        return Optional.of(new Tuple<>(leftSlice, rightSlice));
    }

    private static Result<StringBuilder, ApplicationException> compileContent(List<String> classMembers) {
        Result<StringBuilder, ApplicationException> outputContent = new Ok<>(new StringBuilder());
        for (String classMember : classMembers) {
            var stripped = classMember.strip();
            if (stripped.isEmpty()) continue;
            outputContent = outputContent.and(() -> compileClassMember(stripped)).mapValue(tuple -> tuple.left().append(tuple.right()));
        }
        return outputContent;
    }

    private static Result<String, ApplicationException> compileClassMember(String classMember) {
        return new Err<>(new ApplicationException("Invalid class member: " + classMember));
    }

    private static Optional<Result<String, ApplicationException>> compilePackage(String input) {
        return input.startsWith(PACKAGE_KEYWORD_WITH_SPACE) ? Optional.of(new Ok<>("")) : Optional.empty();
    }

    private static Optional<Result<String, ApplicationException>> compileImport(String input) {
        if (!input.startsWith(IMPORT_KEYWORD_WITH_SPACE)) return Optional.empty();
        var afterKeyword = input.substring(IMPORT_KEYWORD_WITH_SPACE.length());

        if (!afterKeyword.endsWith(STATEMENT_END)) return Optional.empty();
        return Optional.of(new Ok<>(input));
    }

    public static String renderImport(String leftPadding, String parent, String child) {
        return leftPadding + IMPORT_KEYWORD_WITH_SPACE + parent + IMPORT_SEPARATOR + child + STATEMENT_END;
    }

    public static String renderPackage(String name) {
        return PACKAGE_KEYWORD_WITH_SPACE + name + STATEMENT_END;
    }

    static String renderFunction(String modifiers, String name, String content) {
        return modifiers + CLASS_KEYWORD_WITH_SPACE + "def " + name + "() =>" + renderBlock(content);
    }

    static String renderClass(String modifiers, String name) {
        return modifiers + CLASS_KEYWORD_WITH_SPACE + name + renderBlock("");
    }
}
