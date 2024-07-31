package magma;

import java.util.List;

public class MagmaLang {
    public static final String EXPORT_KEYWORD_WITH_SPACE = "export ";
    public static final String FUNCTION = "function";

    static Rule createMagmaRootMemberRule() {
        return new DisjunctionRule(List.of(
                CommonLang.createImportRule(CommonLang.IMPORT, CommonLang.IMPORT_KEYWORD_WITH_SPACE),
                createStatementRule())
        );
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
        var modifiers = new DisjunctionRule(List.of(new StringRule(CommonLang.MODIFIERS), EmptyRule.EMPTY_RULE));
        var name = new StringRule(CommonLang.NAME);
        var content = new DisjunctionRule(List.of(new NodeRule(CommonLang.CONTENT, statement), EmptyRule.EMPTY_RULE));
        var wrappedContent = new PrefixRule(String.valueOf(Splitter.BLOCK_START), new SuffixRule(content, String.valueOf(Splitter.BLOCK_END)));
        var right = new FirstRule(name, "() => ", wrappedContent);
        return new TypeRule(FUNCTION, new FirstRule(modifiers, "def ", right));
    }
}
