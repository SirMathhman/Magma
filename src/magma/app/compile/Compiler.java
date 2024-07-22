package magma.app.compile;

import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.rule.*;

import java.util.List;

public class Compiler {
    public static final String PACKAGE_KEYWORD_WITH_SPACE = "package ";
    public static final char STATEMENT_END = Splitter.STATEMENT_END;
    public static final String IMPORT_KEYWORD_WITH_SPACE = "import ";
    public static final String STRUCT = "struct";
    public static final String EMPTY_CONTENT = " {}";
    public static final String INTERFACE = "interface";
    public static final String PUBLIC_KEYWORD_WITH_SPACE = "public ";
    public static final String EXPORT_KEYWORD_WITH_SPACE = "export ";
    public static final String MODIFIERS = "modifiers";
    public static final String NAME = "name";
    public static final Rule INTERFACE_RULE = createStructRule(INTERFACE);
    public static final Rule STRUCT_RULE = createStructRule(STRUCT);
    public static final String SEGMENTS = "segments";
    public static final String LEADING = "leading";
    public static final String IMPORT = "import";
    public static final Rule IMPORT_RULE = createImportRule();
    public static final OrRule MAGMA_ROOT_MEMBER = new OrRule(List.of(IMPORT_RULE, STRUCT_RULE));
    public static final List<Rule> JAVA_ROOT_MEMBER = List.of(IMPORT_RULE, INTERFACE_RULE);

    private static Rule createStructRule(String keyword) {
        var modifiers = new ExtractRule(MODIFIERS);
        var name = new ExtractRule(NAME);
        var withModifiers = new FirstRule(modifiers, keyword + " ", new RightRule(name, EMPTY_CONTENT));
        var value = new OrRule(List.of(withModifiers, new LeftRule(keyword + " ", new RightRule(name, EMPTY_CONTENT))));
        return new TypeRule(keyword, value);
    }

    public static Rule createImportRule() {
        var value = new LeftRule(IMPORT_KEYWORD_WITH_SPACE, new RightRule(new ExtractRule(SEGMENTS), String.valueOf(STATEMENT_END)));
        return new TypeRule(IMPORT, new StripRule(LEADING, value, ""));
    }

    public static String compile(String input) throws CompileException {
        var segments = Splitter.split(input);

        var output = new StringBuilder();
        for (var line : segments) {
            output.append(compileRootMember(line.strip()).$());
        }
        return output.toString();
    }

    private static Result<String, CompileException> compileRootMember(String input) throws CompileException {
        if (input.isEmpty() || input.startsWith(PACKAGE_KEYWORD_WITH_SPACE)) return new Ok<>("");

        return new OrRule(JAVA_ROOT_MEMBER).parse(input)
                .mapValue(Compiler::modify)
                .flatMapValue(MAGMA_ROOT_MEMBER::generate);
    }

    private static Node modify(Node node) {
        var oldModifiers = node.findString(MODIFIERS);
        if (oldModifiers.isEmpty()) return node;

        var newModifiers = oldModifiers.get().equals(PUBLIC_KEYWORD_WITH_SPACE) ? EXPORT_KEYWORD_WITH_SPACE : "";
        return node.with(MODIFIERS, newModifiers);
    }

}