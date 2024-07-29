package magma.app.compile;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

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
    public static final String BEFORE_NAME = "before-name";
    public static final String AFTER_NAME = "after-name";
    public static final String CHILDREN = "children";
    public static final String AFTER_ROOT_MEMBER = "after-root-member";
    public static final String BEFORE_ROOT_MEMBER = "before-root-member";

    private static Rule createMagmaRootRule() {
        return new DisjunctionRule(List.of(createImportRule(), createTraitRule()));
    }

    private static Rule createJavaRootRule() {
        return new StripRule(BEFORE_ROOT_MEMBER, new DisjunctionRule(List.of(createPackageRule(),
                createImportRule(),
                createInterfaceRule())), AFTER_ROOT_MEMBER);
    }

    private static Rule createImportRule() {
        return new TypeRule(IMPORT, new PrefixRule(IMPORT_KEYWORD_WITH_SPACE, new StringRule(NAMESPACE)));
    }

    private static Rule createPackageRule() {
        return new TypeRule(PACKAGE, new PrefixRule(PACKAGE_KEYWORD_WITH_SPACE, new StringRule(NAMESPACE)));
    }

    private static Rule createInterfaceRule() {
        var modifiers = new StringRule(MODIFIERS);
        var name = new StripRule(BEFORE_NAME, new StringRule(NAME), AFTER_NAME);
        var members = new SuffixRule(new OptionalRule(MEMBERS, new NodeRule(MEMBERS, METHOD_RULE)), String.valueOf(BLOCK_END));
        var afterKeyword = new FirstRule(name, String.valueOf(BLOCK_START), members);
        return new TypeRule(INTERFACE, new FirstRule(modifiers, INTERFACE_KEYWORD_WITH_SPACE, afterKeyword));
    }

    private static Node modify(Node node) {
        if (!node.is(INTERFACE)) return node;
        return node.retype(TRAIT)
                .withString(AFTER_NAME, " ")
                .mapString(MODIFIERS, modifiers -> modifiers.equals(PUBLIC_KEYWORD_WITH_SPACE) ? EXPORT_KEYWORD_WITH_SPACE : "");
    }

    static Rule createTraitRule() {
        var modifiers = new OptionalRule(MODIFIERS, new StringRule(MODIFIERS));
        var name = new StripRule(BEFORE_NAME, new StringRule(NAME), AFTER_NAME);

        var members = new NodeRule(MEMBERS, DEFINITION_RULE);
        var content = new SuffixRule(new OptionalRule(MEMBERS, members), String.valueOf(BLOCK_END));
        var afterKeyword = new FirstRule(name, String.valueOf(Splitter.BLOCK_START), content);
        return new TypeRule(TRAIT, new FirstRule(modifiers, TRAIT_KEYWORD_WITH_SPACE, afterKeyword));
    }

    static String renderMethod(String name) {
        return VOID_KEYWORD_WITH_SPACE + name + EMPTY_PARAMS + STATEMENT_END;
    }

    private static CompileResult<Node> parse(String input, Rule rule) {
        var rootMembers = new Splitter(input).split().toList();

        var list = new ArrayList<Node>();
        for (var rootMember : rootMembers) {
            var stripped = rootMember.strip();
            if (stripped.isEmpty()) continue;

            var parsed = rule.parse(rootMember);
            if (parsed.isInvalid()) return parsed;

            parsed.result().findValue().ifPresent(list::add);
        }

        return new CompileResult<>(new Ok<>(new Node().withNodeList(CHILDREN, list)));
    }

    public String compile(String input) throws CompileException {
        return parse(input, createJavaRootRule()).match(root -> {
            var parsed = root.findNodeList(CHILDREN)
                    .orElseThrow();

            var modified = new ArrayList<Node>();
            for (Node node : parsed) {
                if (node.is(PACKAGE)) continue;
                modified.add(modify(node));
            }

            return getStringCompileExceptionResult(modified);
        }, (BiFunction<CompileException, List<CompileResult<Node>>, Result<String, CompileException>>) (e, compileResults) -> {
            print(e, compileResults, 0);
            return new Err<>(new CompileException("Failed to parse root", input));
        }).$();
    }

    private <T> void print(CompileException e, List<CompileResult<T>> results, int depth) {
        var indent = "\t".repeat(depth);
        System.err.println(indent + depth + ": " + e.getMessage().replace("\n", "\n" + indent));

        for (var result : results) {
            var error = result.findError();
            error.ifPresent(compileException -> print(compileException, result.children(), depth + 1));
        }
    }

    private Result<String, CompileException> getStringCompileExceptionResult(ArrayList<Node> modified) {
        try {
            return new Ok<>(generate(modified));
        } catch (CompileException e) {
            return new Err<>(e);
        }
    }

    private String generate(List<Node> parsed) throws CompileException {
        var builder = new StringBuilder();
        for (Node node : parsed) {
            Rule rule = createMagmaRootRule();
            builder.append(rule.generate(node).result().$());
        }
        return builder.toString();
    }
}