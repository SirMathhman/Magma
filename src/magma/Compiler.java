package magma;

import java.util.ArrayList;
import java.util.List;

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
    public static final String CONTENT = "content";
    public static final String CLASS = "class";
    public static final String FUNCTION = "function";
    public static final String SEGMENTS = "segments";
    public static final String IMPORT = "import";
    public static final String PACKAGE = "package";

    private static Rule createClassMembersRule() {
        return new DisjunctionRule(List.of(
                new TypeRule("empty", EmptyRule.EMPTY_RULE),
                createMethodRule()
        ));
    }

    private static Rule createMethodRule() {
        return new TypeRule(METHOD, new PrefixRule(VOID_KEYWORD_WITH_SPACE, new SuffixRule(new StringRule(NAME), METHOD_SUFFIX)));
    }

    private static Rule createImportRule(String type, String prefix) {
        var segments = new StringRule(SEGMENTS);
        var afterKeyword = new SuffixRule(segments, String.valueOf(Splitter.STATEMENT_END));
        return new TypeRule(type, new PrefixRule(prefix, afterKeyword));
    }

    private static Rule createMagmaRootMemberRule() {
        return new DisjunctionRule(List.of(
                createImportRule(IMPORT, IMPORT_KEYWORD_WITH_SPACE),
                createStatementRule())
        );
    }

    private static Rule createClassRule() {
        var modifiers = new StringRule(MODIFIERS);
        var name = new StripRule(new StringRule(NAME));
        var content = new NodeRule(CONTENT, createClassMembersRule());

        var contentAndEnd = new SuffixRule(content, String.valueOf(Splitter.BLOCK_END));
        var afterKeyword = new FirstRule(name, String.valueOf(Splitter.BLOCK_START), contentAndEnd);
        return new TypeRule(CLASS, new FirstRule(modifiers, CLASS_KEYWORD_WITH_SPACE, afterKeyword));
    }

    static Rule createStatementRule() {
        var statement = new LazyRule();
        statement.set(new DisjunctionRule(List.of(
                createFunctionRule(statement),
                EmptyRule.EMPTY_RULE
        )));

        return statement;
    }

    static Rule createFunctionRule(Rule statement) {
        var modifiers = new DisjunctionRule(List.of(new StringRule(MODIFIERS), EmptyRule.EMPTY_RULE));
        var name = new StringRule(NAME);
        var content = new DisjunctionRule(List.of(new NodeRule(CONTENT, statement), EmptyRule.EMPTY_RULE));
        var wrappedContent = new PrefixRule(String.valueOf(Splitter.BLOCK_START), new SuffixRule(content, String.valueOf(Splitter.BLOCK_END)));
        var right = new FirstRule(name, "() => ", wrappedContent);
        return new TypeRule(FUNCTION, new FirstRule(modifiers, "def ", right));
    }

    private static Rule createJavaRootMemberRule() {
        return new DisjunctionRule(List.of(
                createImportRule(PACKAGE, PACKAGE_KEYWORD_WITH_SPACE),
                createImportRule(IMPORT, IMPORT_KEYWORD_WITH_SPACE),
                createClassRule()
        ));
    }

    private static Result<String, CompileException> generate(List<Node> children) {
        var rootMagmaRule = createMagmaRootMemberRule();
        Result<StringBuilder, CompileException> builder = new Ok<>(new StringBuilder());
        for (Node child : children) {
            builder = builder
                    .and(() -> rootMagmaRule.generate(child).mapErr(err -> err))
                    .mapValue(tuple -> tuple.left().append(tuple.right()));
        }

        return builder.mapValue(StringBuilder::toString);
    }

    private static Result<List<Node>, CompileException> parse(List<String> rootMembers) {
        var javaRootMember = createJavaRootMemberRule();
        Result<List<Node>, CompileException> childrenResult = new Ok<>(new ArrayList<>());
        for (var rootMember : rootMembers) {
            var stripped = rootMember.strip();
            if(stripped.isEmpty()) continue;

            childrenResult = childrenResult
                    .and(() -> javaRootMember.parse(stripped).mapErr(err -> err))
                    .mapValue(Compiler::add);
        }

        return childrenResult;
    }

    private static ArrayList<Node> add(Tuple<List<Node>, Node> tuple) {
        var copy = new ArrayList<>(tuple.left());
        copy.add(tuple.right());
        return copy;
    }

    private static ArrayList<Node> modify(List<Node> list) {
        var copy = new ArrayList<Node>();
        for (Node node : list) {
            if (node.is(PACKAGE)) continue;

            if (node.is(CLASS)) {
                copy.add(node.retype(FUNCTION).mapString(MODIFIERS, oldModifiers -> {
                    var newAccessor = oldModifiers.equals(PUBLIC_KEYWORD_WITH_SPACE) ? EXPORT_KEYWORD_WITH_SPACE : "";
                    return newAccessor + CLASS_KEYWORD_WITH_SPACE;
                }));
            } else {
                copy.add(node);
            }
        }
        return copy;
    }

    String compile(String input) throws CompileException {
        var rootMembers = Splitter.splitRootMembers(input);

        return parse(rootMembers)
                .mapValue(Compiler::modify)
                .flatMapValue(Compiler::generate)
                .$();
    }

}